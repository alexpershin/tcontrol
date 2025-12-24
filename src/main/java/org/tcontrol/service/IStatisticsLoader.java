package org.tcontrol.service;

import org.tcontrol.models.SensorValue;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

public interface IStatisticsLoader {
    List<SensorValue> load() throws FileNotFoundException;
    List<SensorValue> load(long from, long to, Integer sensorId) throws FileNotFoundException;
}
