/*
 */
package com.tcontrol.sensormonitor;

import com.tcontrol.dao.SensorValue;
import java.io.IOException;

/**
 *
 */
public interface MonitorInterface {
    SensorValue loadValue(String sensorUUID) throws IOException;
}
