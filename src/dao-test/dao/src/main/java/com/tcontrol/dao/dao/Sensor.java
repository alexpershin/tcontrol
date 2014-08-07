/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao.dao;

//import java.util.ArrayList;
/**
 *
 * @author Anton Buslavskii
 */
public interface Sensor {

    public static enum sensorType {

        temperature,
        fireAlarm,
        alarm,
        glassBrokeSensor,
        stateGivingSensor

//temperature - returns a double of temperature
//fireAlarm - returns alarm as an excess of the given level (SHOULD BE ABLE TO AUTO CALL TO Java app in a case of alarm)
//alarm - returns alarm as a change of state (boolean)
//glassBrokeSensor - returns alarm as a change of state (boolean)
//stateGivingSensor - - returns alarm as a one option of a set of options
    }

    public static enum sensorState {

        RED, YELLOW, GREEN, GREY;
//RED - alarm on the sensor, 
//YELLOW - not stable connection, trying to define condition of the sensor;
//GREEN - no alarm, everything is OK with sensor's state;
//GREY - sensor is intentionally disabled by user, 
    }

}
