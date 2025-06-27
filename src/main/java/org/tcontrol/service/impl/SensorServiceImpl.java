package org.tcontrol.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.tcontrol.service.SensorService;
import org.tcontrol.service.Sensor;
import org.tcontrol.service.SensorValue;
import org.tcontrol.service.User;
import org.tcontrol.sms.ITemperatureMonitor;
import org.tcontrol.sms.IVoltageMonitor;
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
@ConditionalOnProperty(value = "tcontrol-config.sensorsStub", havingValue = "false")
@AllArgsConstructor
public class SensorServiceImpl implements SensorService {

    private final IVoltageMonitor voltageMonitor;
    private final ITemperatureMonitor temperatureMonitor;
    private final SensorConfig sensorConfig;


    @PostConstruct
    public void init() {

    }

    @Override
    public Map<Integer, Sensor> getAllSensors() {
        return sensorConfig.getSensors().stream()
                .map(sc -> new Sensor(sc.getOrder(), sc.getName(), sc.getType(), sc.getDescription()))
                .collect(Collectors.toMap(Sensor::getId, s -> s));
    }

    @Override
    public Map<Integer, Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(int userId, java.sql.Date date) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return temperatureMonitor.getSensorValueMap().values().stream()
                .map(org.tcontrol.sms.dao.SensorValue::getSensorId)
                .map(sensorId -> {
                    Optional<SensorConfig.SensorConfiguration> sc = sensorConfig.getSensors().stream()
                            .filter(sensor -> sensor.getId().equals(sensorId)).findFirst();
                    return sc;
                }).filter(Optional::isPresent)
                .map(Optional::get)
                .map(sc -> new SensorValue(sc.getOrder(), timestamp, 0.0))
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
