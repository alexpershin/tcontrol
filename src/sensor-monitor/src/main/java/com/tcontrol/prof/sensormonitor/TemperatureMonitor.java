package com.tcontrol.prof.sensormonitor;

import com.tcontrol.dao.SensorValue;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 */
@Stateless
@LocalBean
public class TemperatureMonitor {

    private static final Logger LOGGER = Logger.getLogger(TemperatureMonitor.class.getName());
    
    public final static String W1_DEVICES_PATH = "/sys/bus/w1/devices/";
    public final static String W1_SLAVE = "/w1_slave";

    public SensorValue loadTemperatureValue(String sensorUID) {
        return new SensorValue(-1, System.currentTimeMillis(), Math.random()*30);
    }
}
