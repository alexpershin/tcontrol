package com.tcontrol.sensormonitor;

import com.tcontrol.dao.SensorValue;
import java.io.IOException;
import java.nio.file.Path;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class TemperatureMonitorTest {

    public TemperatureMonitorTest() {
    }

    private TemperatureMonitor monitor;
    private String rootPath;

    @Before
    public void before() {
        monitor = new TemperatureMonitor();
        rootPath = getClass().getResource("/").getFile();
        monitor.setW1DevicesPath(rootPath + "devices");
    }

    @Test
    public void pathTest() {
        final Path fullPathToDevice = monitor.getFullPathToDevice("some_sensor");
        final String expected = rootPath + "devices/some_sensor/" + monitor.getW1SensorInfoFileName();
        assertThat(fullPathToDevice.toString(), is(expected));
    }

    @Test
    public void testSensorValues() throws IOException {
        {
            SensorValue v = monitor.loadTemperatureValue("28-000002c7c3ee");
            assertThat(v.getValue(), is(15.687));
        }
        {
            SensorValue v = monitor.loadTemperatureValue("28-000002c7d1b5");
            assertThat(v.getValue(), is(-1.000));
        }
        {
            SensorValue v = monitor.loadTemperatureValue("28-000002c7eed3");
            assertThat(v.getValue(), is(9.500));
        }
        {
            SensorValue v = monitor.loadTemperatureValue("28-0000066c6a65");
            assertThat(v.getValue(), is(21.562));
        }
    }
}
