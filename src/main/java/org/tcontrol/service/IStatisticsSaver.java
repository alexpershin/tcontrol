package org.tcontrol.service;

import org.tcontrol.service.models.SensorValue;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface IStatisticsSaver {
    void write(List<SensorValue> values);

    Map<Integer, SensorValue> currentValues();

    Deque<SensorValue> lastValues(int sensorId);
}
