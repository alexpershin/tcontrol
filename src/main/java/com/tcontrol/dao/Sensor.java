package com.tcontrol.dao;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Anton Buslavskii
 */
@Getter
@Setter
public class Sensor {

    /**
     * Sensor id.
     */
    private int id;
    /**
     * Sensor name.
     */
    private String name;
    /**
     * Sensor type.
     */
    private SensorType type;

    /**
     * Lower threshold of value to change the sensor state.
     */
    private double lowThreshold;

    /**
     * Hight threshold of value to change the sensor state.
     */
    private double highThreshold;
    
    /**
     * Sensor's hardware serial number.
     */
    private String serialNumber;

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

    public enum SensorType {

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
        ON_OFF,
        /**
         * voltage in double
         */
        VOLTAGE
    }

}
