package com.controller;

import org.tcontrol.controller.SensorController;
import org.tcontrol.controller.dto.SensorValueDto;
import org.tcontrol.dao.Sensor;
import org.tcontrol.dao.SensorValue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.tcontrol.controller.dto.SensorValueDto.SensorValueState.NORMAL;
import static org.tcontrol.controller.dto.SensorValueDto.SensorValueState.WARNING;
import static org.tcontrol.controller.dto.SensorValueDto.SensorValueState.ALERT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.tcontrol.dao.Sensor.SensorType.*;

public class SensorControllerTest {

    private SensorValue value;
    private Sensor sensor;
    private SensorValueDto.SensorValueState state;//result

    public static Stream<Arguments> data() {
        int id = 1;
        long time = System.currentTimeMillis();

        return Stream.of(
                Arguments.of(
                        new SensorValue(id, time, 20.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 12.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 11.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 10.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 9.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 8.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 27.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 28.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 29.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        WARNING
                ),
                Arguments.of(
                        new SensorValue(id, time, 30.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 31.0),
                        new Sensor(id, "Indoor", TEMPERATURE, "", 10, 30, 2),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 230.0),
                        new Sensor(id, "Temperatue", TEMPERATURE, "", 210, 240, 2),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 1),
                        new Sensor(id, "Close", ALARM, ""),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 0),
                        new Sensor(id, "Close", ALARM, ""),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.6),
                        new Sensor(id, "Close", ALARM, ""),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.0),
                        new Sensor(id, "OnOff", ALARM, ""),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 1),
                        new Sensor(id, "OnOff", ON_OFF, ""),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 0),
                        new Sensor(id, "OnOff", ON_OFF, ""),
                        NORMAL
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.6),
                        new Sensor(id, "OnOff", ON_OFF, ""),
                        ALERT
                ),
                Arguments.of(
                        new SensorValue(id, time, 0.0),
                        new Sensor(id, "OnOff", ON_OFF, ""),
                        NORMAL
                )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void calculateStateTest(SensorValue value,
                                   Sensor sensor,
                                   SensorValueDto.SensorValueState state) {

        assertEquals(SensorController.calculateSate(value, sensor), state);
    }
}
