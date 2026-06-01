package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.models.SystemAlert;
import org.tcontrol.service.IStatisticsManager;
import org.tcontrol.service.SystemAlertsAggregator;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.tcontrol.service.impl.SensorServiceImpl.SYSTEM_ALERTS;

@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
@Service
@Slf4j
@RequiredArgsConstructor
public class SystemAlertsAggregatorImpl implements SystemAlertsAggregator {

    public static final int WINDOW_PERIOD = 3600 * 24 * 1000;
    private final SensorConfig sensorConfig;


    @Setter
    private IStatisticsManager statisticsManager = null;

    private final Map<String, SystemAlert> alerts = new ConcurrentHashMap<>();

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
        SystemAlert alert = alerts.computeIfAbsent(message, k -> new SystemAlert(timestamp, message, 0));
        alert.increment();
        alert.setLastTimestamp(timestamp);
        checkAlerts();
    }

    @Override
    public Collection<SystemAlert> alerts() {
        return Collections.unmodifiableCollection(alerts.values());
    }

    @Scheduled(cron = "${alerts.schedule}")
    public void checkAlerts() {
        long size = actualAlertsSize();

        SensorValueShort sensorValue = new SensorValueShort(
                alertSensor.getId(),
                alertSensor.getCode(),
                System.currentTimeMillis(),
                size);

        if (statisticsManager != null) {
            statisticsManager.write(Collections.singletonList(sensorValue));
        }else{
            log.error("Couldn't save alert");
        }
        log.info("actual alerts: {}", size);

        cleanOld();
    }

    private long actualAlertsSize() {
        return alerts().stream().map(SystemAlert::getCount).map(Long::valueOf).reduce(Long::sum).orElse(0L);
    }

    private void cleanOld() {
        Iterator<SystemAlert> it = alerts.values().iterator();
        int n = 0;
        while (it.hasNext()) {
            SystemAlert alert = it.next();
            if (alert.getLastTimestamp() < System.currentTimeMillis() - WINDOW_PERIOD * 2) {
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
