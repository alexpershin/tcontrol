package org.tcontrol.service;

import org.tcontrol.models.SensorValueShort;

import java.util.List;

public interface ICSVDataSaver {
    void write(List<? extends SensorValueShort> values);
}
