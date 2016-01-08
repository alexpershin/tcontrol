package com.tcontrol.prof.sensormonitor;

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

    public SensorValue requestValue() {
        return new SensorValue(1, System.currentTimeMillis(), Math.random()*30);
    }
}
