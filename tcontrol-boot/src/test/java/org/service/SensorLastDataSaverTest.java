package org.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.ICSVDataSaver;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.SystemAlertsAggregator;
import org.tcontrol.service.impl.SensorLastDataManager;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;
import org.tcontrol.service.ITimer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SensorLastDataSaverTest {
    private SensorLastDataManager sensorLastDataSaver;
    private SensorConfig sensorConfig;

    private ITimer timer;

    @BeforeEach
    public void before() {
        timer = mock(ITimer.class);
        when(timer.getCurrentTime()).thenReturn(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));
        when(timer.getCurrentTimeMillis()).thenReturn(4L);

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
        SystemAlertsAggregator systemAlertsAggregator = mock(SystemAlertsAggregator.class);
        sensorLastDataSaver = new SensorLastDataManager(sensorConfig, statisticsLoader, csvDataSaver, timer, systemAlertsAggregator);
        sensorLastDataSaver.setExpiredMillis(3);
    }

    @Test
    public void test() {
        List<SensorValue> values = Collections.singletonList(
                new SensorValue(1, "abc", 1L, 1.0)
        );
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 1);
        assertEquals(sensorLastDataSaver.currentValues().size(), 1);
    }

    @Test
    public void test2() {
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 1L, 1.0)
        );
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 2);
    }

    @Test
    public void test3() {
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 1L, 1.0),
                new SensorValue(2, "abc2", 2L, 2.0)
        );
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 2);
        assertEquals(sensorLastDataSaver.currentValues().get(1).getValue(), 1.0);
        assertEquals(sensorLastDataSaver.currentValues().get(2).getValue(), 2.0);
    }

    @Test
    public void testCapacity() {
        List<SensorValue> values = Arrays.asList(
                new SensorValue(1, "abc1", 1L, 1.0),
                new SensorValue(2, "abc2", 0L, 1.0),
                new SensorValue(2, "abc2", 1L, 2.0),
                new SensorValue(2, "abc2", 2L, 3.0),
                new SensorValue(2, "abc2", 3L, 4.0)
        );
        sensorLastDataSaver.write(values);
        assertEquals(sensorLastDataSaver.currentValues().size(), 2);
        assertEquals(sensorLastDataSaver.currentValues().get(1).getValue(), 1.0);
        assertEquals(sensorLastDataSaver.currentValues().get(2).getValue(), 4.0);
        assertEquals(sensorLastDataSaver.lastValues(1).size(), 1);
        assertEquals(sensorLastDataSaver.lastValues(2).size(), 3);
    }

    @Test
    public void emptyTest() {
        assertEquals(sensorLastDataSaver.lastValues(1).size(), 0);
    }
}
