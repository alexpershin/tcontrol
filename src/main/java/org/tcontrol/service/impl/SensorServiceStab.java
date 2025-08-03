package org.tcontrol.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.config.props.SensorConfiguration;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stub
 */
@Component
@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
public class SensorServiceStab extends SensorServiceImpl {

    public SensorServiceStab(SensorConfig sensorConfig, RelayPinsConfig pinsConfig, IStatisticsSaver sensorLastDataSaver, SensorMapper sensorMapper) {
        super(sensorConfig, pinsConfig, sensorLastDataSaver, sensorMapper, new ArrayList<>());
    }

    @PostConstruct
    public void init() {
        List<SensorConfiguration> sensors = Stream.of(
                new SensorConfiguration(1, "s1", "Indoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Дом",
                        "Inside home sensor", 10, 31, 2),
                new SensorConfiguration(2, "s2", "Outdoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Улица",
                        "Outdor temp sensor", -20, 32, 2),
                new SensorConfiguration(3, "s3", "Cellar",
                        Sensor.SensorType.TEMPERATURE,
                        "Кладовая",
                        "Cellar temp sensor", 0, 20, 3),
                new SensorConfiguration(4, "s4", "Loft",
                        Sensor.SensorType.TEMPERATURE,
                        "Чердак",
                        "Loft temp sensor", -20, 40, 2),
                new SensorConfiguration(5, "s5", "Bath Room",
                        Sensor.SensorType.TEMPERATURE,
                        "Ванная",
                        "Bath Room temp sensor", 5, 34, 2),
                new SensorConfiguration(6, "s6", "Garage",
                        Sensor.SensorType.TEMPERATURE,
                        "Гараж",
                        "Garage temp sensor", -40, 60, 6),
                new SensorConfiguration(7, "s7", "Power",
                        Sensor.SensorType.VOLTAGE,
                        "Напряжение сети",
                        "Power voltage", 190, 250, 10),
                new SensorConfiguration(8, "s8", "Heating",
                        Sensor.SensorType.ON_OFF,
                        "Отопление",
                        "Heating switcher"),
                new SensorConfiguration(9, "s9", "Doors&Windows",
                        Sensor.SensorType.ALARM,
                        "Двери",
                        "Doors and windows close circuit")
        ).collect(Collectors.toList());

        sensorConfig.setSensors(sensors);
    }

    @Override
    public int startHeating(int sensorId, float temperature) {
        SensorValue sv = sensorLastDataSaver.lastValues(sensorId).getLast();
        SensorConfiguration sc = getSc(sensorId);
        if (sv != null) {
            sv.setState(SensorValue.SensorState.ON);
            sv.setValue(1);
            sensorLastDataSaver.write(Collections.singletonList(sv));
            return 1;
        }
        return -1;
    }

    @Override
    public int stopHeating(int sensorId) {
        SensorValue sv = sensorLastDataSaver.lastValues(sensorId).getLast();
        SensorConfiguration sc = getSc(sensorId);

        if (sv != null ) {
            sv.setState(SensorValue.SensorState.OFF);
            sv.setValue(0);
            sensorLastDataSaver.write(Collections.singletonList(sv));
            return 0;
        }
        return -1;
    }

    private SensorConfiguration getSc(int sensorId) {
        return sensorConfig.getSensors().stream()
                .filter(s -> s.getId() == sensorId & s.getType() == Sensor.SensorType.ON_OFF)
                .findFirst()
                .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Sensor config not found by sensorId: {0} and type {1}", sensorId, Sensor.SensorType.ON_OFF)));
    }
}
