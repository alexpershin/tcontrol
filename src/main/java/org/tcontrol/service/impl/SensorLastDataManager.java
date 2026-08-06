package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.*;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toMap;
import static org.tcontrol.models.SensorValue.SensorState.*;

@RequiredArgsConstructor
@Logging
public class SensorLastDataManager implements IStatisticsManager {

    private final SensorConfig sensorConfig;
    private final IStatisticsLoader statisticsLoader;
    private final ICSVDataSaver csvWriter;
    private final ITimer timer;
    private final SystemAlertsAggregator systemAlertsAggregator;

    @Setter
    @Value("${main-config.sensorValuesBufferExpiredMillis:3600000}")
    private int expiredMillis;

    @Setter
    @Value("${gradientDelta}")
    private int gradientDelta = 15;

    private final Map<Integer, Deque<SensorValueShort>> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadPrevData() throws FileNotFoundException {
        systemAlertsAggregator.setStatisticsManager(this);
        fillCache(statisticsLoader.loadLast());
    }

    @Override
    public void write(List<? extends SensorValueShort> values) {
        csvWriter.write(values);
        fillCache(values);
    }

    private void fillCache(List<? extends SensorValueShort> values) {
        for (SensorValueShort sensorValue : values) {
            Deque<SensorValueShort> deque = cache.computeIfAbsent(
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
                    SensorValueShort lastValueShort = values.getLast();
                    SensorValue lastValue = new SensorValue(
                            lastValueShort.getSensorId(),
                            lastValueShort.getSensorCode(),
                            lastValueShort.getTimestamp(),
                            lastValueShort.getValue()
                    );
                    Iterator<SensorValueShort> it = values.descendingIterator();
                    lastValue.setState(calcSate(lastValue, sensorConfig));
                    lastValue.setMinValue(null);
                    lastValue.setMaxValue(null);
                    while (it.hasNext()) {
                        SensorValueShort curValue = it.next();
                        long deltaT = timer.getCurrentTimeMillis() - curValue.getTimestamp();
                        double value = curValue.getValue();
                        calcGradient(deltaT, gradientDelta, lastValue, curValue);
                        calcMinMax(lastValue, value, curValue.getTimestamp(), deltaT);
                    }
                    return lastValue;
                }).collect(toMap(SensorValue::getSensorId, sv -> sv));
    }

    private static void calcGradient(long deltaT, int gradientDelta, SensorValue lastValue, SensorValueShort curValue) {
        if (lastValue.getTimestamp() != curValue.getTimestamp()
                && deltaT >= (long) gradientDelta * 60 * 1000 && deltaT <= (long) (gradientDelta) * 2 * 60 * 1000) {// gradientDelta in мин
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
                    if (Sensor.SensorType.ALARM == sensorConfiguration.getType()) {
                        result = value.getValue() == 0 ? NORMAL : (value.getValue() < 1.0 ? WARNING : ALARM);
                    } else if (Sensor.SensorType.ON_OFF == sensorConfiguration.getType()) {
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
    public Deque<SensorValueShort> lastValues(int sensorId) {
        Deque<SensorValueShort> sensorValues = cache.get(sensorId);
        return sensorValues != null ? sensorValues : new LinkedList<>();
    }

    @Override
    public Collection<SensorValueShort> historyValues(long from, long to, Integer sensorId) throws FileNotFoundException {
        return statisticsLoader.load(from, to, sensorId);
    }
}
