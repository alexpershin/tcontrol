package org.tcontrol.service;

import org.tcontrol.models.SensorValueShort;

import java.io.FileNotFoundException;
import java.util.List;

public interface IStatisticsLoader {
    List<SensorValueShort> loadLast() throws FileNotFoundException;
    List<SensorValueShort> load(long from, long to, Integer sensorId) throws FileNotFoundException;
}
