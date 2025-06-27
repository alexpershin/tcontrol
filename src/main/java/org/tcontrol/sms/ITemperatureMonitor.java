package org.tcontrol.sms;

import org.tcontrol.sms.models.SensorValue;

import java.util.Map;

public interface ITemperatureMonitor {
    Map<String, SensorValue> getSensorValueMap();
    void readSensors();
}
