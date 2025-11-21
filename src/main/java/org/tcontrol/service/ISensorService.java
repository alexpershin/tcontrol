package org.tcontrol.service;

import org.tcontrol.models.ThermostatTemperature;
import org.tcontrol.models.Sensor;
import org.tcontrol.models.SensorValue;
import org.tcontrol.models.User;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface ISensorService {

    /**
     * Get all sensors in the system.
     *
     * @return map id->sensor;
     * @throws SensorServiceException ;
     */
    Map<Integer, Sensor> getAllSensors() throws SensorServiceException;

    /**
     * Get sensors by identifiers in Sensor objects.
     *
     * @param ids identifiers;
     * @return map of sensors with all available parameters;
     */
    Map<Integer, Sensor> getSensors(List<Integer> ids);

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
     * @param sensor a sensor to be saved;
     */
    void saveSensor(Sensor sensor);

    /**
     * Adds the specified sensor to specified user
     *
     * @param sensorId sensor id;
     * @param userId user id;
     */
    void addSensorToUser(int sensorId, int userId);

    /**
     * Removes the sensor with specified ID from user with specified ID
     *
     * @param userId user's identifier;
     * @param sensorId sensor's identifiers;
     */
    void removeSensorFromUser(int userId, int sensorId);

    /**
     * Returns all sensors linked to a user with a given user ID
     *
     * @param userId user's identifier;
     * @return map id->sensor;
     * @throws SensorServiceException ;
     */
    Map<Integer, Sensor> getUserSensors(int userId) throws SensorServiceException;

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
     * @param userId user's identifier;
     */
    void removeUser(int userId);

    /**
     * Returns next unique number for entity identifiers in the project; Use DB
     * sequence here.
     *
     * @return next unique number;
     */
    int nextId();

    /**
     * Returns last(current) values for sensors belong to specified user.
     *
     * @param userId the user which sensor values to be extracted from the
     * store;
     * @return list of sensor values;
     */
    List<SensorValue> getCurrentValues(final int userId, Date date);

    /**
     * Returns last(current) values for sensors belong to specified user.
     *
     * @param userId the user which sensor values to be extracted from the
     * store;
     * @param sensorId sensor's identifier;
     *
     * @return list of sensor values;
     */
    List<SensorValue> getPrevSensorValues(final int userId, Date date, final int sensorId);

    /**
     * Change thermostats temperature option to start/stop heating;
     * @param temperatures list of thermostats and its temperatures;
     * @return 0/1 if one of thermostats stared;
     */
    int changeThermostatOptions(List<ThermostatTemperature> temperatures) ;

    /**
     * Получение текущей температуры термостата для отображения на фронте.
     *
     * @param sensorId сенсор;
     * @return temperature;
     */
    List<ThermostatTemperature> thermostatCurrentTemperature(int sensorId);

    /**
     * Откат термостата на дефолтные настройки.
     *
     * @param sensorId сенсор;
     */
    void thermostatsRollback(Integer sensorId);
}
