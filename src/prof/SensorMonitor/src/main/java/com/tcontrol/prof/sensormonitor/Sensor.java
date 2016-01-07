package com.tcontrol.prof.sensormonitor;

/**
 *
 * @author Anton Buslavskii
 */
public class Sensor {

    private int id;
    private String name;
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

    public double getThresholdLag() {
        return thresholdLag;
    }

    public void setThresholdLag(double thresholdLag) {
        this.thresholdLag = thresholdLag;
    }

    /**
     * TODO Rename SD - Switching differential Threshold delta. When value is
     * getting closer to a threshold the state can be set to pre alarm.
     */
    private double thresholdLag;

    private String description;

    public Sensor() {
    }

    public Sensor(int id, String name, SensorType type, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Sensor(int id, String name, SensorType type, String description,
            double lowThreshold, double highThreshold, double thresholdLag) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.thresholdLag = thresholdLag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SensorType getType() {
        return type;
    }

    public void setType(SensorType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static enum SensorType {

        /**
         * temperature in double
         *//**
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
        ON_OFF,
        /**
         * voltage in double
         */
        VOLTAGE
    }

}
