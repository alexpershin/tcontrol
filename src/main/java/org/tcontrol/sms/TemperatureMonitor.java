package org.tcontrol.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.impl.CSVStatisticSaver;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.sms.config.props.SensorConfig;
import org.tcontrol.sms.config.props.SensorConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemperatureMonitor implements ITemperatureMonitor {
    final private Map<String, SensorValue> sensorValueMap = new HashMap<>();

    private final ITemperatureReader temperatureReader;

    private final SensorConfig sensorConfig;

    private final CSVStatisticSaver csvWriter;

    private final IStatisticsSaver sensorLastDataSaver;

    @Scheduled(cron = "${sensor-config.schedule}")
    public void readSensors() {
        log.info("Reading temperature sensors...");

        int successCount = 0;
        List<SensorConfiguration> sensors = sensorConfig.getSensors();
        List<SensorValue> values = new ArrayList<>();
        for (SensorConfiguration sensor : sensors) {
            try {
                int sensorId = sensor.getId();
                String sensorCode = sensor.getCode();
                SensorValue value = temperatureReader.loadValue(sensorCode);
                value.setSensorCode(sensorCode);
                value.setSensorId(sensorId);
                sensorValueMap.put(sensorCode, value);
                values.add(value);
                successCount++;
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
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
