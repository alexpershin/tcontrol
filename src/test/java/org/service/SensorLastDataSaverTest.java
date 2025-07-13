package org.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcontrol.service.impl.SensorLastDataSaver;
import org.tcontrol.service.models.SensorValue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SensorLastDataSaverTest {
    private SensorLastDataSaver sensorLastDataSaver;

    @BeforeEach
    public void before() {
        sensorLastDataSaver = new SensorLastDataSaver();
        sensorLastDataSaver.setCapacity(3);
    }

    @Test
    public void test() {
        SensorValue[] values = List.of(
                new SensorValue(1, "abc", 1L, 1.0)
        ).toArray(new SensorValue[0]);
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 1);
        assertEquals(sensorLastDataSaver.currentValues().size(), 1);
    }

    @Test
    public void test2() {
        SensorValue[] values = List.of(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 1L, 1.0)
        ).toArray(new SensorValue[0]);
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 2);
    }

    @Test
    public void test3() {
        SensorValue[] values = List.of(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 1L, 1.0),
                new SensorValue(2, "abc2", 2L, 2.0)
        ).toArray(new SensorValue[0]);
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 2);
        assertEquals(sensorLastDataSaver.currentValues().get(1).getValue(), 1.0);
        assertEquals(sensorLastDataSaver.currentValues().get(2).getValue(), 2.0);
    }

    @Test
    public void testCapacity() {
        SensorValue[] values = List.of(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 1L, 1.0),
                new SensorValue(2, "abc2", 2L, 2.0),
                new SensorValue(2, "abc2", 1L, 3.0),
                new SensorValue(2, "abc2", 1L, 4.0)
        ).toArray(new SensorValue[0]);
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 2);
        assertEquals(sensorLastDataSaver.currentValues().get(1).getValue(), 1.0);
        assertEquals(sensorLastDataSaver.currentValues().get(2).getValue(), 4.0);
        assertEquals(sensorLastDataSaver.lastValues(1).size(), 1);
        assertEquals(sensorLastDataSaver.lastValues(2).size(), 3);
    }
}
