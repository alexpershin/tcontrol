package org.tcontrol.hardware.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.config.props.SensorConfiguration;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.hardware.ITemperatureReader;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.impl.CSVStatisticSaver;
import org.tcontrol.service.models.SensorValue;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
public class TemperatureMonitorStub implements ITemperatureMonitor {
    final private Map<String, SensorValue> sensorValueMap = new HashMap<>();

    private final ITemperatureReader temperatureReader;

    private final SensorConfig sensorConfig;

    private final CSVStatisticSaver csvWriter;

    private final IStatisticsSaver sensorLastDataSaver;

    @PostConstruct
    public void postconstruct(){
        int successCount = 0;
        List<SensorConfiguration> sensors = sensorConfig.getSensors();
        List<SensorValue> values = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        List<SensorValue> sensorValues = Arrays.asList(
                new SensorValue(0, "s0", timestamp, 0.0),
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
        for (SensorConfiguration sensor : sensors) {
            int sensorId = sensor.getId();
            String sensorCode = sensor.getCode();
            SensorValue value = sensorValues.get(sensorId);
            value.setSensorCode(sensorCode);
            value.setSensorId(sensorId);
            sensorValueMap.put(sensorCode, value);
            values.add(value);
            successCount++;
        }
        csvWriter.write(values);
        sensorLastDataSaver.write(values);

        log.info("Reading temperature completed(read {} sensors)", successCount);
    }

    @Override
    public Map<String, SensorValue> getSensorValueMap() {
        return sensorValueMap;
    }
}
