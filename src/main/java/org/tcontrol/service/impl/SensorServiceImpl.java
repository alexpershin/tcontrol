package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.mappers.SensorValueMapper;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.SensorService;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.service.models.User;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.config.props.SensorConfig;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * DAO stub to use while real DAO has not been completely implemented.
 */
@Slf4j
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {

    protected final ThermostatsConfig thermostatsConfig;
    protected final SensorConfig sensorConfig;
    private final RelayPinsConfig relayPinsConfig;
    protected final IStatisticsSaver sensorLastDataSaver;
    protected final SensorMapper sensorMapper;
    protected final Map<String,IThermostat> dynamicThermostats;
    protected final SensorValueMapper sensorValueMapper;

    @PostConstruct
    public void init(){
        log.info("init");
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
                .map(sensorCode -> sensorConfig.getCodeToSensorConfMap().get(sensorCode)
                ).filter(Objects::nonNull)
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
    public int startHeating(int sensorId, float temperature) throws SensorServiceException {
        return thermostat(sensorId).on(temperature).checkTemperature() ? 1 : 0;
    }

    @Override
    public int stopHeating(int sensorId) throws SensorServiceException {
        thermostat(sensorId).off().checkTemperature();
        return 0;
    }

    @Override
    public float thermostatCurrentTemperature(int sensorId) {
        log.info("Getting thermostat current temperature for sensor:{}", sensorId);
        return thermostat(sensorId).currentTemperature();
    }

    private IThermostat thermostat(int sensorId) {
        return ofNullable(sensorConfig.getIdToSensorConfMap().get(sensorId))
                .filter(Objects::nonNull)
                .map(sc -> ofNullable(relayPinsConfig.getIdToRelayPinConfigMap().get(Integer.valueOf(sc.getCode())))
                        .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Relay pin not found by sensorCode: {0}", sc.getCode())))
                )
                .map(r -> dynamicThermostats.values().stream()
                        .filter(t -> t.getHeatingPins().contains(r.getPin()))
                        .findFirst()
                        .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Thermostat not found by pin: {0}", r.getPin())))
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
