package org.tcontrol.hardware;

import org.tcontrol.models.SensorValue;

import java.io.IOException;

public interface ITemperatureReader {
    default void load(){}
    SensorValue getValue(String sensorCode) throws IOException;
}
