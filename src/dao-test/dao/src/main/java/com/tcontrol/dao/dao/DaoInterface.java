/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao.dao;

//import java.sql.Time;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

/**
 *
 * @author Anton Buslavskii
 */
public interface DaoInterface {
//    TODO: correlate all input parameters and all parameters being returned

    /**
     * returns all sensors ID for the specified user
     * @param userId user identifier;
     * @return all sensor identifiers, null if user doesn't exists;
     */
    List<Integer> getSensors(int userId); 

    /**
     * Returns last(current) values for sensors belong to specified user.
     * @param userId the user which sensor values to be extracted from the store;
     * @return list of SensorValues;
     */
    List<SensorValue> getCurrentValues(int userId); 
    
    /** Adds values to sensor with specified ID
     *
     * @param sensorId
     * @param values
     */
    void addValues(int sensorId, ArrayList<SensorValue> values); 

    /** Adds a new sensor of specified type
     *
     * @param sensorType type of sensor to be added;
     * @return return sensorId of new sensor, NULL otherwise;
     */
    Integer addSensor(String sensorType);// we specify the type of the added sensor as it name

    /**
     * Adds the specified sensor to specified user
     * @param sensorId
     * @param userId
     */
    void addSensorToUser(int sensorId, int userId); 

    /** Removes the sensor with specified ID from user with specified ID
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
    List<Integer> getSensorsLinkedToUser(int userId); 
    
    //boolean checkFreedomOfSensor(int sensorId); //true if the sensor is now NOT related to any

    //int checkReferenceOfSensorToUser(int sensorId); //returns int Id of the user the sensor is related to

//int newUserAssignment(String userLogin, String userPassword, String userName, String userSurname, String userRole); //assign a new user in a DB, returns an Id of newly added user, by default the role is user
//boolean userDeletion(int userId); //true if the user is marked as deleted and all his/her sensors are not related to this user anymore
//TODO: Add getters and setters for 
}
