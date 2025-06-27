package org.tcontrol.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.tcontrol.service.SensorService;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.service.models.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DAO stub to use while real DAO has not been completely implemented.
 */
@Component
@ConditionalOnProperty(value = "tcontrol-config.sensorsStub", havingValue = "true")
public class SensorServiceStab implements SensorService {

    private Map<Integer, Sensor> sensors;
    private Map<Integer, SensorValue> sensorValues;

    @PostConstruct
    public void init() {
        sensors = Stream.of(
                new Sensor(1, "Indoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Inside home sensor", 10, 31, 2),
                new Sensor(2, "Outdoor",
                        Sensor.SensorType.TEMPERATURE,
                        "Outdor temp sensor", -20, 32, 2),
                new Sensor(3, "Cellar",
                        Sensor.SensorType.TEMPERATURE,
                        "Cellar temp sensor", 0, 20, 3),
                new Sensor(4, "Loft",
                        Sensor.SensorType.TEMPERATURE,
                        "Loft temp sensor", -20, 40, 2),
                new Sensor(5, "Bath Room",
                        Sensor.SensorType.TEMPERATURE,
                        "Bath Room temp sensor", 5, 34, 2),
                new Sensor(6, "Garage",
                        Sensor.SensorType.TEMPERATURE,
                        "Garage temp sensor", -40, 60, 6),
                new Sensor(7, "Power",
                        Sensor.SensorType.VOLTAGE,
                        "Power voltage", 190, 250, 10),
                new Sensor(8, "Heating",
                        Sensor.SensorType.ON_OFF,
                        "Heating switcher"),
                new Sensor(9, "Doors&Windows",
                        Sensor.SensorType.ALARM,
                        "Doors and windows close circuit")
        ).collect(Collectors.toMap(Sensor::getId, s -> s));

        final Timestamp timestamp = new Timestamp(new Date().getTime());
        sensorValues = Stream.of(
                new SensorValue(1, timestamp, 25.5),
                new SensorValue(2, timestamp, -22.6),
                new SensorValue(3, timestamp, +2.4),
                new SensorValue(4, timestamp, -11.4),
                new SensorValue(5, timestamp, +23.7),
                new SensorValue(6, timestamp, -7.4),
                new SensorValue(7, timestamp, 241),
                new SensorValue(8, timestamp, 0.0),
                new SensorValue(9, timestamp, 1.0)
        ).collect(Collectors.toMap(SensorValue::getSensorId, sv -> sv));
    }

    @Override
    public Map<Integer, Sensor> getAllSensors() {
        return sensors;
    }

    @Override
    public Map<Integer, Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(int userId, java.sql.Date date) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return sensorValues.values().stream()
                .peek(sv -> sv.setTimestamp(timestamp))
                .collect(Collectors.toList());
    }

    @Override
    public int startProcess(int sensorId) {
        SensorValue sv = sensorValues.get(sensorId);
        Sensor s = sensors.get(sensorId);
        if (sv != null && s != null && s.getType() == Sensor.SensorType.ON_OFF) {
            sv.setState(SensorValue.SensorState.ON);
            sv.setValue(1);
            sensorValues.put(sensorId, sv);
            return 1;
        }
        return -1;
    }

    @Override
    public int stopProcess(int sensorId) {
        SensorValue sv = sensorValues.get(sensorId);
        Sensor s = sensors.get(sensorId);
        if (sv != null && s != null && s.getType() == Sensor.SensorType.ON_OFF) {
            sv.setState(SensorValue.SensorState.OFF);
            sensorValues.put(sensorId, sv);
            sv.setValue(0);
            return 0;
        }
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
