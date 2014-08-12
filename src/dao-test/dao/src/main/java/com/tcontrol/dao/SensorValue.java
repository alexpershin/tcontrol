package com.tcontrol.dao;

/**
 *
 * @author Anton Buslavskii
 */
public class SensorValue {

    private int sensorId = 0;
    private long timestamp;
    private double value;

    public SensorValue() {
    }

    public SensorValue(long timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
