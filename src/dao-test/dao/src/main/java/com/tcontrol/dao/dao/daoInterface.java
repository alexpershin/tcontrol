/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao.dao;

//import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Anton Buslavskii
 */
public interface daoInterface {
//    TODO: correlate all input parameters and all parameters being returned

    SensorValue[] getSensors(int userId); //returns all sensors ID for the specified user. We get userId and return all user's sensors

    List<SensorValue> getCurrentValues(int userId, int[] sensorIds); //returns list of SensorValues
//TODO: realize this method: List<SensorValue> getValues(int userId, int[] SensorIdList, Date FromPeriod, Date UpToPeriod); // returns all values for listed sensor on specified period of time

    void addValues(int sensorId, ArrayList<Double> Values); //add values with specified ID, TODO: add datetime to each value

    void addSensor(String SensorType);// we specify the type of the added sensor as it name

    boolean addSensorToUser(int sensorId, int userId); //true if the sensor is now related to user with specified ID

    void removeSensorFromUser(int sensorId); //true if the sensor is now removed from the user with specified ID and NOT related to any user

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
