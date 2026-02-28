package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.controller.mappers.SensorMapper;
import org.tcontrol.controller.mappers.SensorValueMapper;
import org.tcontrol.models.*;
import org.tcontrol.service.*;
import org.tcontrol.config.props.SensorConfig;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO stub to use while real DAO has not been completely implemented.
 */
@Slf4j
@RequiredArgsConstructor
@Logging
public class SensorServiceImpl implements ISensorService {

    public static final String SYSTEM_ALERTS = "SYSTEM_ALERTS";
    protected final ThermostatsConfig thermostatsConfig;
    protected final SensorConfig sensorConfig;
    private final RelayPinsConfig relayPinsConfig;
    private final HistoricDataCache historicDataCache;
    private final SystemAlertsAggregator systemAlertsAggregator;
    protected final IStatisticsManager statisticsManager;
    protected final SensorMapper sensorMapper;
    protected final Map<String, IThermostat> dynamicThermostats;
    protected final SensorValueMapper sensorValueMapper;

    @PostConstruct
    public void init() {
        log.trace("init");
    }

    @Override
    public Map<Integer, Sensor> getAllSensors() {
        return sensorConfig.getSensors().stream()
                .map(sensorMapper::map)
                .collect(Collectors.toMap(Sensor::getId, s -> s));
    }

    @Override
    public Map<Integer, Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(int userId, java.sql.Date date) {
        final Map<Integer, SensorValue> idToSensorValueMap = statisticsManager.currentValues();
        return idToSensorValueMap.values().stream()
                .map(SensorValue::getSensorId)
                .map(sensorConfig::sensorConfigById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(sc -> {
                    SensorValue sensorValue = idToSensorValueMap.get(sc.getId());
                    return sensorValueMapper.clone(sensorValue);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SensorValueShort> getPrevSensorValues(int userId, int sensorId) {
        return statisticsManager.lastValues(sensorId)
                .stream()
                .map(v -> new SensorValueShort(
                        v.getSensorId(),
                        v.getSensorCode(),
                        v.getTimestamp(),
                        v.getValue())
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<SensorValueShort> getHistorySensorValues(int currentUserId, long from, long to, Integer sensorId) throws FileNotFoundException {
        return historicDataCache.historyValues(from, to, sensorId)
                .stream()
                .map(v -> new SensorValue(
                        v.getSensorId(),
                        v.getSensorCode(),
                        v.getTimestamp(),
                        v.getValue())
                )
                .collect(Collectors.toList());
    }

    @Override
    public Collection<SystemAlert> alerts(int sensorId) {

        List<SensorConfig.SensorConfiguration> appropriateSensor = sensorConfig.getSensors().stream()
                .filter(sensorConfiguration ->
                        sensorConfiguration.getName().equals(SYSTEM_ALERTS)
                                && sensorConfiguration.getType() == Sensor.SensorType.ALARM).collect(Collectors.toList());

        if (appropriateSensor.isEmpty()) {
            String message = MessageFormat.format("No good config for sensor: {0}", sensorId);
            log.error(message);
            throw new SensorServiceException(message);
        } else if (appropriateSensor.size() != 1) {
            String message = MessageFormat.format("Duplicate config for sensor: {0}", sensorId);
            log.error(message);
            throw new SensorServiceException(message);
        } else {
            return systemAlertsAggregator.alerts();
        }
    }

    @Override
    public int changeThermostatOptions(List<ThermostatTemperature> temperatures) throws SensorServiceException {
        return temperatures.stream()
                .map(temp -> dynamicThermostats.values().stream()
                        .filter(t -> t.getCode().equals(temp.getThermostatCode())).findFirst()
                        .map(t -> t.temperature(temp.getDayTemperature()).checkTemperature() ? 1 : 0)
                        .orElseThrow(() ->
                                new SensorServiceException(MessageFormat.format("Thermostat not found by pin: {0}",
                                        joining(temperatures))))
                )
                .reduce((a, b) -> a | b).orElseThrow(() ->
                        new SensorServiceException(MessageFormat.format("No thermostat processed: {0}",
                                joining(temperatures))));
    }

    private static String joining(List<ThermostatTemperature> temperatures) {
        return temperatures.stream()
                .map(ThermostatTemperature::getThermostatName)
                .collect(Collectors.joining("'"));
    }

    @Override
    public List<ThermostatTemperature> thermostatCurrentTemperature(int sensorId) {
        return thermostats(sensorId).stream()
                .map(t -> new ThermostatTemperature(
                        t.getCode(),
                        t.getName(),
                        t.currentTemperature(),
                        t.getThermostatConfig().getTDay(),
                        t.getThermostatConfig().getTNight(),
                        t.getThermostatConfig().nightBegin(),
                        t.getThermostatConfig().nightEnd()))
                .collect(Collectors.toList());
    }

    @Override
    public int thermostatsRollback(Integer sensorId) {
        return thermostats(sensorId).stream()
                .map(thermostat -> thermostat.rollback().checkTemperature() ? 1 : 0)
                .reduce((a, b) -> a | b)
                .orElseThrow(() ->
                        new SensorServiceException(MessageFormat.format("No thermostat found: {0}",
                                sensorId)));
    }

    private List<IThermostat> thermostats(int sensorId) {
        return sensorConfig.sensorConfigById(sensorId)
                .map(SensorConfig.SensorConfiguration::getCode)
                .map(Integer::valueOf)
                .map(code -> relayPinsConfig.pinConfigurationById(code)
                        .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Relay pin not found by sensorCode: {0}", code)))
                )
                .map(r -> dynamicThermostats.values().stream()
                        .filter(t -> t.getHeatingPins().contains(r.getPin()))
                        .collect(Collectors.toList())

                ).orElseThrow(() -> new SensorServiceException(MessageFormat.format("SensorConfig not found by sensorId: {0}", sensorId)));
    }

    @Override
    public void addValues(List<SensorValue> values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer addSensor(String sensorType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveSensor(Sensor sensor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSensorToUser(int sensorId, int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSensorFromUser(int userId, int sensorId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<Integer, Sensor> getUserSensors(int userId) {
        //throw new DaoException(new UnsupportedOperationException("Not supported yet."));
        return getAllSensors();
    }

    @Override
    public void addUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeUser(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
