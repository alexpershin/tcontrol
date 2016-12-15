package com.tcontrol.dao;

import com.tcontrol.dao.Sensor.SensorType;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author alexey
 */
public class SensorTest {
    
    public SensorTest() {
    }

    @Test
    public void testSensorTypeToString() {
        assertThat(SensorType.TEMPERATURE.toString(),is("TEMPERATURE"));
    }
    
    @Test
    public void testSensorCreationAndFill() {
        Sensor sensor=new Sensor();
        sensor.setSerialNumber("1");
        assertThat(sensor.getSerialNumber(), is("1"));
    }
    
}
