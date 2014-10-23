package com.tcontrol.dao;

import java.sql.Timestamp;

/**
 *
 * @author Anton Buslavskii
 */
public class SensorValue {

    private int sensorValueId = 0;
    private int sensorId = 0;
    private Timestamp timestamp;
    private double value;

    public SensorValue() {
    }

    public SensorValue(Timestamp timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    
     public SensorValue(double value) {
        this.value = value;
    }
    

    public int getSensorValueId() {
        return sensorValueId;
    }

    public void setSensorValueId(int sensorValueId) {
        this.sensorValueId = sensorValueId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
