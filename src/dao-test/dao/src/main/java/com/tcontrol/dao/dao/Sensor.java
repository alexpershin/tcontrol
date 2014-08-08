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

        /** 
         * temperature in double
         */
        temp,

        /**
         * alarm as a change of state
         */
        alarm,
        
        /**
         * on or off
         */
        onoff,

        /**
         * voltage in double
         */
        voltage
    }

    public static enum sensorState {

        RED, YELLOW, GREEN, GREY;
//RED - alarm on the sensor, 
//YELLOW - not stable connection, trying to define condition of the sensor;
//GREEN - no alarm, everything is OK with sensor's state;
//GREY - sensor is intentionally disabled by user, 
    }

}
