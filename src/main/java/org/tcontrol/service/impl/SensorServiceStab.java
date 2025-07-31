package org.tcontrol.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.config.props.SensorConfiguration;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DAO stub to use while real DAO has not been completely implemented.
 */
@Component
@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
public class SensorServiceStab extends SensorServiceImpl {

    public SensorServiceStab(SensorConfig sensorConfig, IStatisticsSaver sensorLastDataSaver, SensorMapper sensorMapper) {
        super(sensorConfig, sensorLastDataSaver, sensorMapper);
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

        final long timestamp = System.currentTimeMillis();
        List<SensorValue> sensorValues = Arrays.asList(
                new SensorValue(1, "s1", timestamp, 25.5),
                new SensorValue(2, "s2", timestamp, -22.6),
                new SensorValue(3, "s3", timestamp, +2.4),
                new SensorValue(4, "s4", timestamp, -11.4),
                new SensorValue(5, "s5", timestamp, +23.7),
                new SensorValue(6, "s6", timestamp, -7.4),
                new SensorValue(7, "s7", timestamp, 241),
                new SensorValue(8, "s8", timestamp, 0.0),
                new SensorValue(9, "s9", timestamp, 1.0)
        );
        sensorLastDataSaver.write(sensorValues);
    }

    @Override
    public int startProcess(int sensorId) {
        SensorValue sv = sensorLastDataSaver.lastValues(sensorId).getLast();
        SensorConfiguration sc = sensorConfig.getSensors().stream().filter(s->s.getId()==sensorId).findFirst().orElse(null);
        if (sv != null && sc != null && sc.getType() == Sensor.SensorType.ON_OFF) {
            sv.setState(SensorValue.SensorState.ON);
            sv.setValue(1);
            sensorLastDataSaver.write(Collections.singletonList(sv));
            return 1;
        }
        return -1;
    }

    @Override
    public int stopProcess(int sensorId) {
        SensorValue sv = sensorLastDataSaver.lastValues(sensorId).getLast();
        SensorConfiguration sc = sensorConfig.getSensors().stream().filter(s->s.getId()==sensorId).findFirst().orElse(null);

        if (sv != null && sc != null && sc.getType() == Sensor.SensorType.ON_OFF) {
            sv.setState(SensorValue.SensorState.OFF);
            sv.setValue(0);
            sensorLastDataSaver.write(Collections.singletonList(sv));
            return 0;
        }
        return -1;
    }
}
