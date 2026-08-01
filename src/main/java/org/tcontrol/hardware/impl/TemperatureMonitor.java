package org.tcontrol.hardware.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.tcontrol.config.Logging;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.IStatisticsManager;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;
import org.tcontrol.hardware.ITemperatureReader;
import org.tcontrol.config.props.SensorConfig;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.Optional.*;
import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

@Slf4j
@RequiredArgsConstructor
@Logging
public class TemperatureMonitor implements ITemperatureMonitor {
    //To return value as new
    public static final int DELAY_TIME = 30000;

    @Getter
    final private Map<String, SensorValue> sensorCodeToValueMap = new HashMap<>();

    private final ITemperatureReader temperatureReader;

    private final SensorConfig sensorConfig;

    private final IStatisticsManager statisticsManager;

    @PostConstruct
    public void init() {
        log.trace("init");
    }

    @Scheduled(cron = "${sensor-config.schedule}")
    public void readSensors() {
        temperatureReader.load();
        int successCount = 0;
        List<SensorConfiguration> sensors = sensorConfig.getSensors();
        List<SensorValue> values = new ArrayList<>();

        for (SensorConfiguration sensorConfiguration : sensors) {
            if (sensorConfiguration.getType() == Sensor.SensorType.TEMPERATURE) {
                try {
                    int sensorId = sensorConfiguration.getId();
                    String sensorCode = sensorConfiguration.getCode();
                    SensorValue value = temperatureReader.getValue(sensorCode);
                    if (value != null) {
                        value.setSensorCode(sensorCode);
                        value.setSensorId(sensorId);
                        sensorCodeToValueMap.put(sensorCode, value);
                        values.add(value);
                        successCount++;
                    } else {
                        log.warn("No value found for sensorConfiguration: {}", sensorCode);
                    }
                } catch (RuntimeException e) {
                    log.warn(e.getMessage());
                }
            }
        }
        statisticsManager.write(values);

        log.info("Reading temperature completed(read {} sensors)", successCount);
    }

    @Override
    public SensorValue getValueBySensorCode(String sensorCode) {
        return of(sensorCode)
                .map(sensorCodeToValueMap::get)
                .filter(sv -> sv.getTimestamp() > System.currentTimeMillis() - DELAY_TIME)
                .orElseGet(() ->readSensorValue(sensorCode));
    }

    private SensorValue readSensorValue(String sensorCode) {
        SensorValue sv = temperatureReader.getValue(sensorCode);
        sensorCodeToValueMap.put(sensorCode, sv);
        statisticsManager.write(Lists.newArrayList(sv));
        return sv;
    }
}
