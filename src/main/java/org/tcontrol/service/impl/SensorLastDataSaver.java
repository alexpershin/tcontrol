package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.ITimer;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.tcontrol.models.SensorValue.SensorState.*;

@Component
@RequiredArgsConstructor
@Logging
public class SensorLastDataSaver implements IStatisticsSaver {

    private final SensorConfig sensorConfig;
    private final IStatisticsLoader statisticsLoader;
    private final ITimer timer;

    @Setter
    @Value("${main-config.sensorValuesBufferExpiredMillis:3600000}")
    private int expiredMillis;

    private final Map<Integer, Deque<SensorValue>> cache = new HashMap<>();

    @PostConstruct
    public void loadPrevData() throws FileNotFoundException {
        write(statisticsLoader.load());
    }

    @Override
    public void write(List<SensorValue> values) {
        for (SensorValue sensorValue : values) {
            Deque<SensorValue> deque = cache.computeIfAbsent(
                    sensorValue.getSensorId(),
                    k -> new LinkedList<>()
            );
            deque.addLast(sensorValue);
            if (timer.getCurrentTimeMillis() - deque.peekFirst().getTimestamp() > expiredMillis) {
                deque.removeFirst();
            }
        }
    }

    @Override
    public Map<Integer, SensorValue> currentValues() {

        return cache.values().stream()
                .filter(values -> !values.isEmpty())
                .map(values -> {
                    SensorValue lastValue = values.getLast();
                    Iterator<SensorValue> it = values.descendingIterator();
                    lastValue.setState(calcSate(lastValue, sensorConfig));
                    lastValue.setMinValue(null);
                    lastValue.setMaxValue(null);
                    while (it.hasNext()) {
                        SensorValue curValue = it.next();
                        long deltaT = timer.getCurrentTimeMillis() - curValue.getTimestamp();
                        double value = curValue.getValue();
                        calcGradient(deltaT, lastValue, curValue);
                        calcMinMax(lastValue, value, curValue.getTimestamp(), deltaT);
                    }
                    return lastValue;
                }).collect(toMap(SensorValue::getSensorId, sv -> sv));
    }

    private static void calcGradient(long deltaT, SensorValue lastValue, SensorValue curValue) {
        if (lastValue != curValue && deltaT >= 15 * 60 * 1000 && deltaT <= 20 * 60 * 1000) {// 15-20мин
            double gradient = lastValue.getValue() - curValue.getValue();
            lastValue.setGradient(gradient);
        }
    }

    private static void calcMinMax(SensorValue lastValue, double value, long timestamp, long deltaT) {
        if (deltaT <= 60 * 60 * 1000 * 24) {// 24 hours
            if (lastValue.getMinValue() == null) {
                lastValue.setMinValue(value);
                lastValue.setMinValueTimestamp(timestamp);
                lastValue.setMaxValue(value);
                lastValue.setMaxValueTimestamp(timestamp);
            } else {
                if (lastValue.getMinValue() > value) {
                    lastValue.setMinValue(value);
                    lastValue.setMinValueTimestamp(timestamp);
                }
                if (lastValue.getMaxValue() < value) {
                    lastValue.setMaxValue(value);
                    lastValue.setMaxValueTimestamp(timestamp);
                }
            }
        }
    }

    public static SensorValue.SensorState calcSate(SensorValue value, SensorConfig sensorConfig) {
        return sensorConfig.sensorConfigById(value.getSensorId())
                .map(sensorConfiguration -> {
                    double v = value.getValue();
                    SensorValue.SensorState result = NORMAL;
                    if (Sensor.SensorType.ALARM == sensorConfiguration.getType()
                            || Sensor.SensorType.ON_OFF == sensorConfiguration.getType()) {
                        result = value.getValue() == 0 ? NORMAL : ALARM;

                    } else //calculate state according to threshold
                        //low threshold
                        if (value.getValue() <= sensorConfiguration.getLowThreshold()) {
                            result = ALARM;
                        } else if (v <= sensorConfiguration.getLowThreshold() + sensorConfiguration.getThresholdLag()
                                && v > sensorConfiguration.getLowThreshold()) {
                            result = WARNING;
                        } //high threshold
                        else if (v >= sensorConfiguration.getHighThreshold()) {
                            result = ALARM;
                        } else if (v >= sensorConfiguration.getHighThreshold() - sensorConfiguration.getThresholdLag()
                                && v < sensorConfiguration.getHighThreshold()) {
                            result = WARNING;
                        }
                    return result;
                })
                .orElse(NORMAL);//if configuration not defined, values associated with relay-pins
    }


    @Override
    public Deque<SensorValue> lastValues(int sensorId) {
        Deque<SensorValue> sensorValues = cache.get(sensorId);
        return sensorValues != null ? sensorValues : new LinkedList<>();
    }
}
