package org.tcontrol.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Sensor value to be passed to web UI.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SensorValueDto {

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

    private Double minValue;
    private long minValueTimestamp;

    private Double maxValue;
    private long maxValueTimestamp;

    /**
     * Returns current value of the sensor.
     * @param sensorId sensor's id;
     * @param timestamp time stamp when value was got;
     * @param value the value;
     * @param state the value state;
     * @param gradient the gradient;
     */
    public SensorValueDto(int sensorId, long timestamp,
                          double value, SensorValueState state, Double gradient) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
        this.state = state;
        this.gradient = gradient;
    }

    /**
     * Sensors wrapper to be passed to web UI.
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @Schema(description = "Объект с информацией о сенсорах")
    public static class SensorsResult {

        private SensorDto[] sensors;
    }
}
