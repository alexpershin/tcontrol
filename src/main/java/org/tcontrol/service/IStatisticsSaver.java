package org.tcontrol.service;

import org.tcontrol.service.models.SensorValue;

import java.util.List;
import java.util.Map;

public interface IStatisticsSaver {
    void write(SensorValue[] values);

    Map<Integer, SensorValue> currentValues();

    List<SensorValue> lastValues(int sensorId);
}
