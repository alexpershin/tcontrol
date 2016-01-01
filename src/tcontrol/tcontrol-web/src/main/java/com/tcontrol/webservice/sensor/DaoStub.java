package com.tcontrol.webservice.sensor;

import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import com.tcontrol.dao.User;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

/**
 * DAO stub.
 */
@ApplicationScoped
public class DaoStub implements DaoInterface {

    @Override
    public Map<Integer, Sensor> getAllSensors() {
        Map<Integer, Sensor> result = new HashMap<Integer, Sensor>();
        for (Sensor sensor : Arrays.asList(
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
                        "Heating switcher"))) {
            result.put(sensor.getId(), sensor);
        };
        return result;
    }

    @Override
    public Map<Integer, Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(int userId) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return Arrays.asList(
                new SensorValue(1, timestamp, 25.5),
                new SensorValue(2, timestamp, -22.6),
                new SensorValue(3, timestamp, +2.4),
                new SensorValue(4, timestamp, -11.4),
                new SensorValue(5, timestamp, +23.7),
                new SensorValue(6, timestamp, -7.4),
                new SensorValue(7, timestamp, 241),
                new SensorValue(8, timestamp, 1));
    }

    @Override
    public void addValues(int sensorId, ArrayList<SensorValue> values) {
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
    public List<Integer> getUserSensors(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
