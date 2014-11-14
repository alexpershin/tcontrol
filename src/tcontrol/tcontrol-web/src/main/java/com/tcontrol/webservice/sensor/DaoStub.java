/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author alexey
 */
public class DaoStub implements DaoInterface {

    @Override
    public Map<Integer,Sensor> getAllSensors() {
        Map<Integer,Sensor> result = new HashMap<Integer,Sensor>();
        for(Sensor sensor: Arrays.asList(
                new Sensor(1, "indor",
                        Sensor.SensorType.TEMPERATURE, "inside home sensor"),
                new Sensor(2, "outdor",
                        Sensor.SensorType.TEMPERATURE, "outdor temp sensor"))){
            result.put(sensor.getId(), sensor);
        };
        return result;
   }

    @Override
    public Map<Integer,Sensor> getSensors(List<Integer> ids) {
       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

    @Override
    public List<SensorValue> getCurrentValues(int userId) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return Arrays.asList(
                new SensorValue(1, timestamp, 23.5),
                new SensorValue(2, timestamp, -10.6));
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
