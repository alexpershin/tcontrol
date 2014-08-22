package com.tcontrol.dao;

/**
 *
 * @author Anton Buslavskii
 */
public class Sensor {

    private int id;
    private int name;
    private SensorType type;
    
    /**
     * Lower threshold of value to change the sensor state.
     */
    private double lowThreshold;
    
    /**
     * Hight threshold of value to change the sensor state.
     */
    private double highThreshold;

    public double getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(double lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    public double getHighThreshold() {
        return highThreshold;
    }

    public void setHighThreshold(double highThreshold) {
        this.highThreshold = highThreshold;
    }

    public double getThresholdDelta() {
        return thresholdDelta;
    }

    public void setThresholdDelta(double thresholdDelta) {
        this.thresholdDelta = thresholdDelta;
    }
    
    /**
     * Threshold delta. 
     * When value is getting closer to a threshold 
     * the state can be set to YELLOW. 
     */
    private double thresholdDelta;
    
    /**
     * State of the sensor.
     * Are not stored in the database.
     * It should be calculated in accordance with the sensor value 
     * and the low and hight threshold. 
     * For example when temperature inside a house is lower than low threshold 
     * set to 12 degrees, the sensor state must be RED, also if 
     * the temperature is higher than 30 degrees it must be RED, 
     * when temperature is between low and high thresholds the state is GREEN.    
     */
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
