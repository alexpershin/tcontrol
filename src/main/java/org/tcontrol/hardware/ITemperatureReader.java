package org.tcontrol.hardware;

import org.tcontrol.service.models.SensorValue;

import java.io.IOException;

public interface ITemperatureReader {
    SensorValue loadValue(String sensorCode) throws IOException;
}
