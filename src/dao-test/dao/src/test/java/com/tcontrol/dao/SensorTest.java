/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.dao;

import com.tcontrol.dao.Sensor.SensorType;
import static org.hamcrest.CoreMatchers.is;
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
    
}
