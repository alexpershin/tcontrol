package org.tcontrol.service.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.sms.models.SensorValue;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component

public class SensorLastDataSaver implements IStatisticsSaver {

    @Setter
    @Value("${sensorValuesBufferSize:10}")
    private int capacity;

    private final Map<Integer, LinkedList<SensorValue>> cache = new HashMap<>();

    @Override
    public void write(SensorValue[] values) {
        for (SensorValue sensorValue : values) {
            var queue = cache.computeIfAbsent(
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
    public Map<Integer, SensorValue> currentValues(){
        return cache.values().stream()
                .map(Deque::peekLast)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SensorValue::getSensorId, sv->sv));
    }

    @Override
    public List<SensorValue> lastValues(int sensorId){
        return cache.get(sensorId);
    }
}
