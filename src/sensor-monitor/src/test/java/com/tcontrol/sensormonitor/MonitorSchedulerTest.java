package com.tcontrol.sensormonitor;

import com.google.common.collect.Maps;
import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import java.io.IOException;
import java.util.HashMap;
import javax.ejb.Timer;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class MonitorSchedulerTest {

    MonitorScheduler monitorScheduler;
    DaoInterface dao;

    @Before
    public void before() throws DaoException {
        monitorScheduler = new MonitorScheduler();
        dao = mock(DaoInterface.class);
        
        final HashMap<Integer, Sensor> sensors = Maps.newHashMap();
        final Sensor sensor = mock(Sensor.class);
        when(sensor.getType()).thenReturn(Sensor.SensorType.TEMPERATURE);
        sensors.put(1, sensor);
        
        when(dao.getAllSensors()).thenReturn(sensors);
    }

    @Test
    public void testLoadTemperatureValueException() throws IOException, DaoException {
        final TemperatureMonitor tMonitor = mock(TemperatureMonitor.class);
        when(tMonitor.loadTemperatureValue(anyString())).thenThrow(new IOException());

        monitorScheduler.temperatureMonitor = tMonitor;
        monitorScheduler.dao = dao;
        monitorScheduler.execute(mock(Timer.class));

        verify(dao, never()).addValues(any());
    }

    @Test
    public void testLoadTemperatureValue() throws IOException, DaoException {
        final TemperatureMonitor tMonitor = mock(TemperatureMonitor.class);
        when(tMonitor.loadTemperatureValue(anyString())).thenReturn(mock(SensorValue.class));

        monitorScheduler.temperatureMonitor = tMonitor;
        monitorScheduler.dao = dao;
        monitorScheduler.execute(mock(Timer.class));

        verify(dao, atLeastOnce()).addValues(any());
    }
}
