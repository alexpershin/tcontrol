package com.dao;

import org.tcontrol.service.Sensor;
import org.tcontrol.service.Sensor.SensorType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author alexey
 */
public class SensorTest {
    
    public SensorTest() {
    }

    @Test
    public void testSensorTypeToString() {
        assertEquals(SensorType.TEMPERATURE.toString(),"TEMPERATURE");
    }
    
    @Test
    public void testSensorCreationAndFill() {
        Sensor sensor=new Sensor();
        sensor.setSerialNumber("1");
        assertEquals(sensor.getSerialNumber(), "1");
    }
    
}
