/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.dao.dao;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Anton Buslavskii
 */
public interface daoInterface {
//    TODO: correlate all input parameters and all parameters being returned
ArrayList<Integer> getSensors(int userId, int[] sensorsIdList); //returns all sensors for the specified user
ArrayList<String> getValues(int userId, int[] SensorIdList, Date FromPeriod, Date UpToPeriod); // returns all values for listed sensor on specified period of time
ArrayList<String> addValues(int sensorId, ArrayList<Double> Values); //add values with specified ID, TODO: add datetime to each value
ArrayList<String> addSensor(int Sensor);// TODO: int is not good here, we should specify the type of the added sensor 
boolean addSensorToUser(int sensorId,int userId); //true if the sensor is now related to user with specified ID
boolean removeSensorFromUser(int sensorId); //true if the sensor is now removed from the user with specified ID and NOT related to any user
boolean checkFreedomOfSensor(int sernsorId); //true if the sensor is now NOT related to any
int checkReferenceOfSensorToUser(int sernsorId); //returns int Id of the user the sensor is related to

int newUserAssignment(String userLogin, String userPassword, String userName, String userSurname, String userRole); //assign a new user in a DB, returns an Id of newly added user, by default the role is user

boolean userDeletion(int userId); //true if the user is marked as deleted and all his/her sensors are not related to this user anymore
//TODO: Add getters and setters for 
}
