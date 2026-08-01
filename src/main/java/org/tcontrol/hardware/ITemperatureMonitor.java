package org.tcontrol.hardware;

import org.tcontrol.models.SensorValue;

import java.util.Map;

public interface ITemperatureMonitor {
    Map<String, SensorValue> getSensorCodeToValueMap();

    default SensorValue getValueBySensorCode(String sensorCode) {
        return getSensorCodeToValueMap().get(sensorCode);
    }
}
