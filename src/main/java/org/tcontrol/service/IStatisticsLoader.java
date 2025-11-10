package org.tcontrol.service;

import org.tcontrol.service.models.SensorValue;

import java.io.FileNotFoundException;
import java.util.List;

public interface IStatisticsLoader {
    List<SensorValue> load() throws FileNotFoundException;
}
