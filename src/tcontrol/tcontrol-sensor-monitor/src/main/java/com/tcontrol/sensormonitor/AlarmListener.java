package com.tcontrol.sensormonitor;
import com.tcontrol.dao.SensorValue;
/**
 *
 * @author alexey
 */
public interface AlarmListener {
    void alarm(SensorValue v);
}
