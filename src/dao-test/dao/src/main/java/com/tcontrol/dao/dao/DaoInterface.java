/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao.dao;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anton Buslavskii
 */
public interface DaoInterface {
//    TODO: correlate all input parameters and all parameters being returned

    /**
     * Get all sensors in the system.
     *
     * @return list of identifiers;
     */
    List<Sensor> getSensors();

    /**
     * Get sensors by identifiers.
     *
     * @param ids
     * @return list of sensors;
     */
    List<Sensor> getSensors(List<Integer> ids);

    /**
     * Returns last(current) values for sensors belong to specified user.
     *
     * @param userId the user which sensor values to be extracted from the
     * store;
     * @return list of sensor values;
     */
    List<SensorValue> getCurrentValues(int userId);

    /**
     * Adds values to sensor with specified ID
     *
     * @param sensorId
     * @param values
     */
    void addValues(int sensorId, ArrayList<SensorValue> values);

    /**
     * Adds a new sensor of specified type
     *
     * @param sensorType type of sensor to be added;
     * @return return sensorId of new sensor, NULL otherwise;
     */
    Integer addSensor(String sensorType);// we specify the type of the added sensor as it name

    /**
     * Saves sensor data to storage;
     *
     * @param sensor
     */
    void saveSensor(Sensor sensor);

    /**
     * Adds the specified sensor to specified user
     *
     * @param sensorId
     * @param userId
     */
    void addSensorToUser(int sensorId, int userId);

    /**
     * Removes the sensor with specified ID from user with specified ID
     *
     * @param userId
     * @param sensorId
     */
    void removeSensorFromUser(int userId, int sensorId);

    /**
     *
     * @param userId
     * @return Returns a list of sensors IDs when gets the user ID as integer
     */
    List<Integer> getSensorIdsLinkedToUser(int userId);

    /**
     * Adds a new user
     *
     * @param user new user to be added;
     */
    void addUser(User user);

    /**
     * Update the user data.
     *
     * @param user the user to be updated
     */
    void updateUser(User user);

    /**
     * Removes the specified user with given ID. All sensors were related with
     * this user become unlinked.
     *
     * @param userId
     */
    void removeUser(int userId);

}
