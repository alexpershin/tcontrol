package com.tcontrol.dao;

import java.sql.Timestamp;

/**
 *
 * @author Anton Buslavskii
 */
public class SensorValue {

    private int sensorValueId;
    private int sensorId;
    private Timestamp timestamp;
    private double value;
    private Double gradient;
    /**
     * State of the sensor. Are not stored in the database. It should be
     * calculated in accordance with the sensor value and the low and hight
     * threshold. For example when temperature inside a house is lower than low
     * threshold set to 12 degrees, the sensor state must be ALARM, also if the
     * temperature is higher than 30 degrees it must be ALARM, when temperature
     * is between low and high thresholds the state is NORMAL.
     */
    private SensorState state;

    public SensorValue() {
    }

    public SensorValue(int sensorId, Timestamp timestamp,
            double value) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
    }
    
    public SensorValue(int sensorId, long time,
            double value) {
        this.sensorId = sensorId;
        this.timestamp = new Timestamp(time);
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

    public SensorState getState() {
        return state;
    }

    public void setState(SensorState state) {
        this.state = state;
    }

    /**
     * @return the gradient
     */
    public Double getGradient() {
        return gradient;
    }

    /**
     * @param gradient the gradient to set
     */
    public void setGradient(Double gradient) {
        this.gradient = gradient;
    }

    public static enum SensorState {

        /**
         * alarm on the sensor.
         */
        ALARM,
        /**
         * pre alarm state.
         */
        WARNING,
        /**
         * no alarm, everything is OK with sensor's state.
         */
        NORMAL,
        /**
         * sensor is intentionally disabled by user, right now it is not
         * implemented yet.
         */
        DISABLED,
        /**
         * sensor is in ON state.
         */
        ON,
        /**
         * sensor is in ON state.
         */
        OFF;

    }
}
