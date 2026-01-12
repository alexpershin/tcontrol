package org.tcontrol.service;

import org.tcontrol.models.SensorValue;

import java.io.FileNotFoundException;
import java.util.Collection;

public interface HistoricDataCache {
    Collection<SensorValue> historyValues(long from, long to, Integer sensorId) throws FileNotFoundException;
}
