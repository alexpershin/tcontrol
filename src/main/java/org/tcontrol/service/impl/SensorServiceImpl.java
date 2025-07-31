package org.tcontrol.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.service.SensorService;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.service.models.User;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.sms.config.props.SensorConfig;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO stub to use while real DAO has not been completely implemented.
 */
@Component
@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "false")
@AllArgsConstructor
public class SensorServiceImpl implements SensorService {

    protected final SensorConfig sensorConfig;
    protected final IStatisticsSaver sensorLastDataSaver;
    protected final SensorMapper sensorMapper;

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
        return sensorLastDataSaver.currentValues().values().stream()
                .map(SensorValue::getSensorCode)
                .map(sensorCode -> sensorConfig.getSensors().stream()
                        .filter(sensor -> sensor.getCode().equals(sensorCode)).findFirst()
                ).filter(Optional::isPresent)
                .map(Optional::get)
                .map(sc ->{
                    SensorValue sensorValue = sensorLastDataSaver.currentValues().get(sc.getId());
                    return new SensorValue(sc.getId(), sc.getCode(), sensorValue.getTimestamp(),
                        sensorValue.getValue());
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
    public int startProcess(int sensorId) {

        return -1;
    }

    @Override
    public int stopProcess(int sensorId) {

        return -1;
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
