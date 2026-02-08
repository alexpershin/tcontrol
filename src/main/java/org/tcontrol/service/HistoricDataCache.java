package org.tcontrol.service;

import org.tcontrol.models.SensorValue;
import org.tcontrol.models.SensorValueShort;

import java.io.FileNotFoundException;
import java.util.Collection;

public interface HistoricDataCache {
    Collection<SensorValueShort> historyValues(long from, long to, Integer sensorId) throws FileNotFoundException;
}
