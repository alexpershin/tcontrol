package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.tcontrol.service.models.SensorValue.SensorState.*;
import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

@Component
@RequiredArgsConstructor
public class SensorLastDataSaver implements IStatisticsSaver {

    private final SensorConfig sensorConfig;

    @Setter
    @Value("${main-config.sensorValuesBufferSize:10}")
    private int capacity;

    private final Map<Integer, LinkedList<SensorValue>> cache = new HashMap<>();

    @Override
    public void write(List<SensorValue> values) {
        for (SensorValue sensorValue : values) {
            LinkedList<SensorValue> queue = cache.computeIfAbsent(
                    sensorValue.getSensorId(),
                    k -> new LinkedList<>()
            );
            queue.add(sensorValue);
            if (queue.size() > capacity) {
                queue.removeFirst();
            }
        }
    }

    @Override
    public Map<Integer, SensorValue> currentValues() {

        final Map<Integer, SensorConfiguration> sensorConfigurationMap =
                sensorConfig.getSensors().stream()
                        .collect(toMap(SensorConfiguration::getId, sc -> sc));
        return cache.values().stream().map(values -> {
            SensorValue lastValue = values.getLast();
            Iterator<SensorValue> it = values.descendingIterator();
            lastValue.setMinValue(null);
            lastValue.setMaxValue(null);
            while (it.hasNext()) {
                SensorValue sv = it.next();
                SensorConfig.SensorConfiguration sensorConfiguration = sensorConfigurationMap.get(sv.getSensorId());
                long deltaT = lastValue.getTimestamp() - sv.getTimestamp();
                double value = sv.getValue();
                calcGradient(deltaT, lastValue, value);
                calcMinMax(lastValue, value, sv.getTimestamp(), deltaT);
                sv.setState(calcSate(sv, sensorConfiguration));
            }
            return lastValue;
        }).collect(toMap(SensorValue::getSensorId, sv -> sv));
    }

    private static void calcGradient(long deltaT, SensorValue lastValue, double value) {
        if (deltaT >= 15 * 60 * 1000 && deltaT <= 20 * 60 * 1000) {// 15-20мин
            double gradient = lastValue.getValue() - value;
            lastValue.setGradient(gradient);
        }
    }

    private static void calcMinMax(SensorValue lastValue, double value, long timestamp, long deltaT) {
        if (deltaT <= 60 * 60 * 1000) {// 60мин
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

    public static SensorValue.SensorState calcSate(SensorValue value,
                                                   SensorConfig.SensorConfiguration sensorConfiguration) {

        SensorValue.SensorState result = NORMAL;

        double v = value.getValue();
        if (Sensor.SensorType.ALARM == sensorConfiguration.getType()
                || Sensor.SensorType.ON_OFF == sensorConfiguration.getType()) {
            result = value.getValue() == 0 ? NORMAL : ALARM;

        } else //calculate state according to threshold
            //low thressHold
            if (value.getValue() <= sensorConfiguration.getLowThreshold()) {
                result = ALARM;
            } else if (v <= sensorConfiguration.getLowThreshold() + sensorConfiguration.getThresholdLag()
                    && v > sensorConfiguration.getLowThreshold()) {
                result = WARNING;
            } //high thressHold
            else if (v >= sensorConfiguration.getHighThreshold()) {
                result = ALARM;
            } else if (v >= sensorConfiguration.getHighThreshold() - sensorConfiguration.getThresholdLag()
                    && v < sensorConfiguration.getHighThreshold()) {
                result = WARNING;
            }
        return result;
    }


    @Override
    public LinkedList<SensorValue> lastValues(int sensorId) {
        LinkedList<SensorValue> sensorValues = cache.get(sensorId);
        return sensorValues != null ? sensorValues : new LinkedList<>();
    }

    @Override
    public SensorValue lastValue(int sensorId) {
        LinkedList<SensorValue> sensorValues = cache.get(sensorId);
        return sensorValues != null ? sensorValues.peekLast() : null;
    }
}
