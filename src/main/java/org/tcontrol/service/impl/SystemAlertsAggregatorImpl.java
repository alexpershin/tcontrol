package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.models.SystemAlert;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.SystemAlertsAggregator;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.tcontrol.service.impl.SensorServiceImpl.SYSTEM_ALERTS;

@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemAlertsAggregatorImpl implements SystemAlertsAggregator {

    public static final int WINDOW_PERIOD = 3600 * 24 * 1000;
    private final SensorConfig sensorConfig;
    private final IStatisticsSaver sensorLastDataSaver;

    private final Collection<SystemAlert> alerts = new ConcurrentLinkedQueue<>();

    private SensorConfig.SensorConfiguration alertSensor;

    @PostConstruct
    public void init() {
        alertSensor = alertSensor();
    }

    @Override
    public void addAlert(String message) {
        addAlert(System.currentTimeMillis(), message);
    }

    @Override
    public void addAlert(long timestamp, String message) {
        log.info("alert added: {}", message);
        alerts.add(new SystemAlert(timestamp, message));
    }

    @Override
    public Collection<SystemAlert> alerts() {
        return Collections.unmodifiableCollection(alerts);
    }

    @Scheduled(cron = "${alerts.schedule}")
    public void checkAlerts() {
        long size = actualAlertsSize();

        SensorValueShort sensorValue = new SensorValueShort(
                alertSensor.getId(),
                alertSensor.getCode(),
                System.currentTimeMillis(),
                ((double) size) / 10.0);
        sensorLastDataSaver.write(Collections.singletonList(sensorValue));
        log.info("actual alerts: {}", size);

        cleanOld();
    }

    private long actualAlertsSize() {
        return alerts().stream()
                .filter(a -> a.getTimestamp() > System.currentTimeMillis() - WINDOW_PERIOD)
                .count();
    }

    private void cleanOld() {
        Iterator<SystemAlert> it = alerts.iterator();
        int n = 0;
        while (it.hasNext()) {
            SystemAlert alert = it.next();
            if (alert.getTimestamp() < System.currentTimeMillis() - WINDOW_PERIOD * 2) {
                it.remove();
                n++;
            }
        }
        log.info("removed: {}", n);
    }

    private SensorConfig.SensorConfiguration alertSensor() {
        log.info("alerts checking...");
        List<SensorConfig.SensorConfiguration> appropriateSensor = sensorConfig.getSensors().stream()
                .filter(sensorConfiguration ->
                        sensorConfiguration.getName().equals(SYSTEM_ALERTS)
                                && sensorConfiguration.getType() == Sensor.SensorType.ALARM).collect(Collectors.toList());

        if (appropriateSensor.size() == 1) {
            return appropriateSensor.get(0);
        } else {
            log.warn("Too many '{}' configs", SYSTEM_ALERTS);
        }
        return null;
    }
}
