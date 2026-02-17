package org.tcontrol.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.models.SystemAlert;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.SystemAlertsAggregator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.tcontrol.service.impl.SensorServiceImpl.SYSTEM_ALERTS;

@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemAlertsAggregatorImpl implements SystemAlertsAggregator {

    private final SensorConfig sensorConfig;
    private final IStatisticsSaver sensorLastDataSaver;

    private final List<SystemAlert> alerts = new ArrayList<>();

    @Override
    public void addAlert(String message) {
        addAlert(System.currentTimeMillis(), message);
    }

    @Override
    public void addAlert(long timestamp, String message) {
        alerts.add(new SystemAlert(timestamp, message));
    }

    @Override
    public List<SystemAlert> alerts() {
        return new ArrayList<>(alerts);
    }

    @Scheduled(cron = "${alerts.schedule}")
    public void checkAlerts() {
        log.info("alerts checking...");
        List<SensorConfig.SensorConfiguration> appropriateSensor = sensorConfig.getSensors().stream()
                .filter(sensorConfiguration ->
                        sensorConfiguration.getName().equals(SYSTEM_ALERTS)
                                && sensorConfiguration.getType() == Sensor.SensorType.ALARM).collect(Collectors.toList());

        if (appropriateSensor.size() == 1) {
            SensorConfig.SensorConfiguration sensorConfiguration = appropriateSensor.get(0);

            List<SystemAlert> actualAlerts = actualAlerts();

            SensorValueShort sensorValue = new SensorValueShort(sensorConfiguration.getId(), sensorConfiguration.getCode(), System.currentTimeMillis(), actualAlerts.size());
            sensorLastDataSaver.write(Collections.singletonList(sensorValue));
            log.info("actual alerts: {}", actualAlerts.size());
        } else {
            log.warn("Too many '{}' configs", SYSTEM_ALERTS);
        }
    }

    @Override
    public List<SystemAlert> actualAlerts() {
        return alerts().stream()
                .filter(a -> a.getTimestamp() > System.currentTimeMillis() - 3600 * 24 * 1000 * 7)
                .collect(Collectors.toList());
    }
}
