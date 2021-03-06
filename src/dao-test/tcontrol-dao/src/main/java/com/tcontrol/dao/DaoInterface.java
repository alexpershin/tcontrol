package com.tcontrol.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface DaoInterface {

    /**
     * Get all sensors in the system.
     *
     * @return map id->sensor;
     * @throws com.tcontrol.dao.DaoException;
     */
    Map<Integer, Sensor> getAllSensors() throws DaoException;

    /**
     * Get sensors by identifiers in Sensor objects.
     *
     * @param ids
     * @return list of sensors with all available parameters;
     */
    Map<Integer, Sensor> getSensors(List<Integer> ids);

    /**
     * Returns last(current) values for sensors belong to specified user.
     *
     * @param userId the user which sensor values to be extracted from the
     * store;
     * @return list of sensor values;
     */
    List<SensorValue> getCurrentValues(int userId);

    /**
     * Adds values to sensor with specified ID.
     *
     * @param values values to be added;
     */
    void addValues(List<SensorValue> values);

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
     * Returns all sensors linked to a user with a given user ID
     *
     * @param userId user's identifier;
     * @return map id->sensor;
     * @throws com.tcontrol.dao.DaoException;
     */
    Map<Integer, Sensor> getUserSensors(int userId) throws DaoException;

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

    /**
     * Returns next unique number for entity identifiers in the project; Use DB
     * sequence here.
     *
     * @return next unique number;
     */
    int nextId();

    List<SensorValue> getCurrentValues(final int userId, Date date);

}
