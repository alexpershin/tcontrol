package org.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.ICSVDataSaver;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.impl.SensorLastDataManager;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SensorLastDataSaverGradientTest {
    private SensorLastDataManager sensorLastDataSaver;
    private SensorConfig sensorConfig;

    private ITimer timer;

    @BeforeEach
    public void before() {
        timer = mock(ITimer.class);
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));
        when(timer.getCurrentTimeMillis()).thenReturn(1000000L);


        List<SensorConfig.SensorConfiguration> sensors = Stream.of(
                new SensorConfig.SensorConfiguration(1, "1", "Indoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Дом",
                        "Inside home sensor", 10, 31, 2),
                new SensorConfig.SensorConfiguration(2, "2", "Outdoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Улица",
                        "Outdor temp sensor", -20, 32, 2)
                ).collect(Collectors.toList());
        sensorConfig = new SensorConfig();
        sensorConfig.setSensors(sensors);
        IStatisticsLoader statisticsLoader = mock(IStatisticsLoader.class);
        ICSVDataSaver csvDataSaver = mock(ICSVDataSaver.class);
        sensorLastDataSaver = new SensorLastDataManager(sensorConfig, statisticsLoader, csvDataSaver, timer);
        sensorLastDataSaver.setExpiredMillis(2000000);
    }

    @Test
    public void testGradient() {
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 100000L, 1.0),
                new SensorValue(2, "abc2", 300000L, 3.0),
                new SensorValue(2, "abc2", 1000000L, 4.0)
        );
        sensorLastDataSaver.write(values);
        Map<Integer, SensorValue> integerSensorValueMap = sensorLastDataSaver.currentValues();
        assertEquals(integerSensorValueMap.size(), 2);
        assertEquals(integerSensorValueMap.get(1).getValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getValue(), 4.0);
        assertNull(integerSensorValueMap.get(1).getGradient());
        assertEquals(integerSensorValueMap.get(2).getGradient(), 3.0);
        assertEquals(integerSensorValueMap.get(1).getMaxValue(), 1.0);
        assertEquals(integerSensorValueMap.get(1).getMaxValueTimestamp(), 1L);
        assertEquals(integerSensorValueMap.get(2).getMaxValue(), 4.0);
        assertEquals(integerSensorValueMap.get(2).getMaxValueTimestamp(), 1000000L);
        assertEquals(integerSensorValueMap.get(1).getMinValue(), 1.0);
        assertEquals(integerSensorValueMap.get(1).getMinValueTimestamp(), 1L);
        assertEquals(integerSensorValueMap.get(2).getMinValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMinValueTimestamp(), 100000L);

    }

    @Test
    public void testGradient2() {
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 20L, 1.0),
                new SensorValue(2, "abc2", 300000L, 3.0),
                new SensorValue(2, "abc2", 1000000L, 4.0)
        );
        sensorLastDataSaver.write(values);
        Map<Integer, SensorValue> integerSensorValueMap = sensorLastDataSaver.currentValues();
        assertEquals(integerSensorValueMap.size(), 2);
        assertEquals(integerSensorValueMap.get(1).getValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getValue(), 4.0);
        assertNull(integerSensorValueMap.get(1).getGradient());
        assertEquals(integerSensorValueMap.get(2).getGradient(), 3.0);
        assertEquals(integerSensorValueMap.get(1).getMaxValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMaxValue(), 4.0);
        assertEquals(integerSensorValueMap.get(1).getMinValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMinValue(), 1.0);
    }

    @Test
    public void testGradient3() {
        when(timer.getCurrentTimeMillis()).thenReturn(1400021L);
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 200021L, 1.0),
                new SensorValue(2, "abc2", 18L, 1.0),
                new SensorValue(2, "abc2", 200019L, 1.1),
                new SensorValue(2, "abc2", 300000L, 3.0),
                new SensorValue(2, "abc2", 1200020L, 4.0)
        );
        sensorLastDataSaver.write(values);
        Map<Integer, SensorValue> integerSensorValueMap = sensorLastDataSaver.currentValues();
        assertEquals(integerSensorValueMap.size(), 2);
        assertEquals(integerSensorValueMap.get(1).getValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getValue(), 4.0);
        assertNull(integerSensorValueMap.get(1).getGradient());
        assertEquals(integerSensorValueMap.get(2).getGradient(), 1.0);
        assertEquals(integerSensorValueMap.get(1).getMaxValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMaxValue(), 4.0);
        assertEquals(integerSensorValueMap.get(1).getMinValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMinValue(), 1.0);
    }

    @Test
    public void testGradient4() {
        when(timer.getCurrentTimeMillis()).thenReturn(1300000L);
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 20L, 1.0),
                new SensorValue(2, "abc2", 100000L, 3.0),
                new SensorValue(2, "abc2", 1300000L, 4.0)
        );
        sensorLastDataSaver.write(values);
        Map<Integer, SensorValue> integerSensorValueMap = sensorLastDataSaver.currentValues();
        assertEquals(integerSensorValueMap.size(), 2);
        assertEquals(integerSensorValueMap.get(1).getValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getValue(), 4.0);
        assertNull(integerSensorValueMap.get(1).getGradient());
        assertEquals(integerSensorValueMap.get(2).getGradient(), 1.0);
    }

    @Test
    public void testGradient5() {
        when(timer.getCurrentTimeMillis()).thenReturn(1300001L);
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 20L, 1.0),
                new SensorValue(2, "abc2", 100000L, 3.0),
                new SensorValue(2, "abc2", 1300001L, 4.0)
        );
        sensorLastDataSaver.write(values);
        Map<Integer, SensorValue> integerSensorValueMap = sensorLastDataSaver.currentValues();
        assertEquals(integerSensorValueMap.size(), 2);
        assertEquals(integerSensorValueMap.get(1).getValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getValue(), 4.0);
        assertNull(integerSensorValueMap.get(1).getGradient());
        assertNull(integerSensorValueMap.get(2).getGradient());
    }

    @Test
    public void testGradient6() {//descending
        when(timer.getCurrentTimeMillis()).thenReturn(1400021L);
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 200021L, 1.0),
                new SensorValue(2, "abc2", 18L, 4.0),
                new SensorValue(2, "abc2", 200019L, 3.1),
                new SensorValue(2, "abc2", 300000L, 2.0),
                new SensorValue(2, "abc2", 1200020L, 1.0)
        );
        sensorLastDataSaver.write(values);
        Map<Integer, SensorValue> integerSensorValueMap = sensorLastDataSaver.currentValues();
        assertEquals(integerSensorValueMap.size(), 2);
        assertEquals(integerSensorValueMap.get(1).getValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getValue(), 1.0);
        assertNull(integerSensorValueMap.get(1).getGradient());
        assertEquals(integerSensorValueMap.get(2).getGradient(), -1.0);
        assertEquals(integerSensorValueMap.get(1).getMaxValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMaxValue(), 4.0);
        assertEquals(integerSensorValueMap.get(1).getMinValue(), 1.0);
        assertEquals(integerSensorValueMap.get(2).getMinValue(), 1.0);
    }

}
