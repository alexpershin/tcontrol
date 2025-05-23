package com.tcontrol.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sensor value to be passed to web UI.
 */
@Getter
@Setter
@NoArgsConstructor
public class SensorValueWeb {

    public enum SensorValueState {
        NORMAL,
        WARNING,
        ALERT
    }
    private int sensorId;
    
    private long timestamp;
    
    private double value;
    
    /**
     * Delta for the last hour.
     */
    private Double gradient;
    
    private SensorValueState state;


    /**
     * Returns current value of the sensor.
     * @param sensorId sensor's id;
     * @param timestamp time stamp when value was got;
     * @param value the value;
     * @param state the value state;
     * @param gradient the gradient;
     */
    public SensorValueWeb(int sensorId, long timestamp, 
            double value, SensorValueState state, Double gradient) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
        this.state = state;
        this.gradient = gradient;
    }


}
