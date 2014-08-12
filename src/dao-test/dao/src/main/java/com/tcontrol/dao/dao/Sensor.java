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
public class Sensor {

    private int id;
    private int name;
    private SensorType type;
    private SensorState state;
    private int description;

    public Sensor() {
    }

    public Sensor(int id, int name, SensorType type, SensorState state, int description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.state = state;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    public SensorState getState() {
        return state;
    }

    public void setState(SensorState state) {
        this.state = state;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public static enum SensorType {

        /**
         * temperature in double
         */
        TEMPERATURE,
        /**
         * alarm as a change of state
         */
        ALARM,
        /**
         * on or off
         */
        ON_OF,
        /**
         * voltage in double
         */
        VOLTAGE
    }

    public static enum SensorState {

        /**
         * alarm on the sensor
         */
        RED,
        /**
         * not stable connection, trying to define condition of the sensor;
         */
        YELLOW,
        /**
         * no alarm, everything is OK with sensor's state;
         */
        GREEN,
        /**
         * sensor is intentionally disabled by user, right now it is not
         * implemented yet
         */
        GREY;

    }

}
