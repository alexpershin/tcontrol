/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.webservice.sensor;

import com.tcontrol.dao.Sensor;
import static com.tcontrol.dao.Sensor.SensorType.TEMPERATURE;
import com.tcontrol.dao.SensorValue;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.ALERT;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.NORMAL;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.WARNING;
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author alexey
 */
@RunWith(Parameterized.class)
public class SensorWebServiceTest {

    private SensorValue value;
    private Sensor sensor;
    private SensorValueWeb.SensorValueState state;//result

    @Parameterized.Parameters
    public static Iterable data() {
        int id = 1;
        long time = System.currentTimeMillis();

        return Arrays.asList(
                new Object[][]{{
                    new SensorValue(id, time, 20.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    NORMAL
                }, {
                    new SensorValue(id, time, 12.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    WARNING
                }, {
                    new SensorValue(id, time, 11.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    WARNING
                }, {
                    new SensorValue(id, time, 10.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    ALERT
                },{
                    new SensorValue(id, time, 9.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    ALERT
                },{
                    new SensorValue(id, time, 8.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    ALERT
                },{
                    new SensorValue(id, time, 27.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    NORMAL
                },{
                    new SensorValue(id, time, 28.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    WARNING
                },{
                    new SensorValue(id, time, 29.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    WARNING
                },{
                    new SensorValue(id, time, 30.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    ALERT
                },{
                    new SensorValue(id, time, 31.0),
                    new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                    ALERT
                },
                }
        );
    }

    public SensorWebServiceTest(
            SensorValue value,
            Sensor sensor,
            SensorValueWeb.SensorValueState state) {
        this.value = value;
        this.sensor = sensor;
        this.state = state;
    }

    @Test
    public void calculateSateTest() {
        assertThat(SensorsWebService.calculateSate(value, sensor), is(state));
    }
}