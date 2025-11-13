package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.controller.mappers.SensorMapper;
import org.tcontrol.controller.mappers.SensorValueMapper;
import org.tcontrol.service.ISensorService;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;
import org.tcontrol.models.ThermostatTemperature;
import org.tcontrol.models.User;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.config.props.SensorConfig;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
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
public class ISensorServiceImpl implements ISensorService {

    protected final ThermostatsConfig thermostatsConfig;
    protected final SensorConfig sensorConfig;
    private final RelayPinsConfig relayPinsConfig;
    protected final IStatisticsSaver sensorLastDataSaver;
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
        final Map<Integer, SensorValue> idToSensorValueMap = sensorLastDataSaver.currentValues();
        return idToSensorValueMap.values().stream()
                .map(SensorValue::getSensorCode)
                .map(sensorConfig::sensorConfigByCode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(sc -> {
                    SensorValue sensorValue = idToSensorValueMap.get(sc.getId());
                    return sensorValueMapper.clone(sensorValue);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SensorValue> getPrevSensorValues(int userId, java.sql.Date date, int sensorId) {
        return sensorLastDataSaver.lastValues(sensorId)
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
    public int changeThermostatOptions(List<ThermostatTemperature> temperatures) throws SensorServiceException {
        return temperatures.stream()
                .map(t->dynamicThermostats.get(t.getThermostatName()).on(t.getTemperature()).checkTemperature() ? 1 : 0)
                .reduce((a,b)->a*b)
                .orElseThrow(() ->
                        new SensorServiceException(MessageFormat.format("Thermostat not found by pin: {0}",
                                temperatures.stream().map(ThermostatTemperature::getThermostatName)).concat(",")));
    }


    @Override
    public List<ThermostatTemperature> thermostatCurrentTemperature(int sensorId) {
        return thermostats(sensorId).stream()
                        .map(t-> new ThermostatTemperature(t.getName(), t.currentTemperature()))
                        .collect(Collectors.toList());
    }

    private IThermostat thermostat(int sensorId, String pin) {
        return dynamicThermostats.values().stream()
                .filter(t -> t.getHeatingPins().contains(pin))
                .findFirst()
                .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Thermostat not found by pin: {0}", pin))
                );
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
                )
                .orElseThrow(() -> new SensorServiceException(MessageFormat.format("SensorConfig not found by sensorId: {0}", sensorId)));
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
