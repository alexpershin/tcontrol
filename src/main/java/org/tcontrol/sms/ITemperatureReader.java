package org.tcontrol.sms;

import org.tcontrol.sms.models.SensorValue;

import java.io.IOException;

public interface ITemperatureReader {
    SensorValue loadValue(String sensorUUID) throws IOException;
}
