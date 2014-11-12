/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.webservice.sensor;

import java.sql.Timestamp;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alexey
 */
@XmlRootElement
public class SensorValueWeb {
    
   public static enum SensorValueState{
        NORMAL,
        WARNING,
        ALERT
    }
    private int sensorId;
    private Timestamp timestamp;
    private double value;
    private SensorValueState state;
    
    /**
     * @return the sensorId
     */
    public int getSensorId() {
        return sensorId;
    }

    /**
     * @param sensorId the sensorId to set
     */
    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * @return the timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the state
     */
    public SensorValueState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(SensorValueState state) {
        this.state = state;
    }
}
