package org.tcontrol.service;

import org.tcontrol.models.SensorValue;
import org.tcontrol.models.SensorValueShort;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public interface IStatisticsManager {
    void write(List<? extends SensorValueShort> values);

    Map<Integer, SensorValue> currentValues();

    Deque<SensorValueShort> lastValues(int sensorId);

    Collection<SensorValueShort> historyValues(long from, long to, Integer sensorId) throws FileNotFoundException;
}
