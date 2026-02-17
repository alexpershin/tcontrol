package org.tcontrol.hardware.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.impl.CSVStatisticSaver;
import org.tcontrol.models.SensorValue;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

@Slf4j
@RequiredArgsConstructor
public class TemperatureMonitorStub implements ITemperatureMonitor {
    final private Map<String, SensorValue> sensorValueMap = new HashMap<>();

    private final SensorConfig sensorConfig;

    private final CSVStatisticSaver csvWriter;

    private final IStatisticsSaver sensorLastDataSaver;

    @PostConstruct
    public void init() {
        int successCount = 0;
        List<SensorConfiguration> sensors = sensorConfig.getSensors();
        List<SensorValue> values = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        List<SensorValue> sensorValues = Arrays.asList(
                new SensorValue(1, "1", timestamp - 900000*4, 24.1, -10.0, 20.0),
                new SensorValue(1, "1", timestamp- 900000*3, 25.5, -10.0, 20.0),
                new SensorValue(1, "1", timestamp- 900000*2, 26.5, -10.0, 20.0),
                new SensorValue(1, "1", timestamp, 24.5, -10.0, 20.0),
                new SensorValue(2, "2", timestamp - 900002, -22.6),
                new SensorValue(2, "2", timestamp, -23.6),
                new SensorValue(3, "3", timestamp, +2.4),
                new SensorValue(4, "4", timestamp, -11.4),
                new SensorValue(5, "5", timestamp, +23.7),
                new SensorValue(6, "6", timestamp, -7.4),
                new SensorValue(7, "7", timestamp - 1, 0),
                new SensorValue(7, "7", timestamp, 241),
                new SensorValue(8, "8", timestamp - 300000 * 4, 0.0),
                new SensorValue(8, "8", timestamp - 300000 * 3, 1.0),
                new SensorValue(8, "8", timestamp - 300000 * 2, 0.0),
                new SensorValue(8, "8", timestamp, 1.0),
                new SensorValue(9, "9", timestamp, 11.0),
                new SensorValue(10, "10", timestamp, 0.1)
        );
        for (SensorConfiguration sensor : sensors) {
            int sensorId = sensor.getId();
            String sensorCode = sensor.getCode();
            List<SensorValue> sensorValuesTransformed = sensorValues.stream()
                    .filter(s -> s.getSensorId() == sensorId)
                    .peek(s -> s.setSensorCode(sensorCode))
                    .collect(Collectors.toList());
            SensorValue value = sensorValuesTransformed.get(sensorValuesTransformed.size() - 1);
            sensorValueMap.put(sensorCode, value);
            values.add(value);
            successCount++;
        }
        csvWriter.write(values);
        sensorLastDataSaver.write(sensorValues);

        log.info("Reading temperature completed(read {} sensors)", successCount);
    }

    @Override
    public Map<String, SensorValue> getSensorValueMap() {
        return sensorValueMap;
    }
}
