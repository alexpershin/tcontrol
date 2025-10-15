package org.tcontrol.service.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Anton Buslavskii
 */
@Getter
@Setter
@NoArgsConstructor
public class SensorValue {

    private int sensorValueId;
    private int sensorId;
    private String pin;
    private String sensorCode;
    private long timestamp;
    private double value;
    private Double gradient;
    private Double minValue;
    private long minValueTimestamp;
    private Double maxValue;
    private long maxValueTimestamp;

    /**
     * State of the sensor. Are not stored in the database. It should be
     * calculated in accordance with the sensor value and the low and hight
     * threshold. For example when temperature inside a house is lower than low
     * threshold set to 12 degrees, the sensor state must be ALARM, also if the
     * temperature is higher than 30 degrees it must be ALARM, when temperature
     * is between low and high thresholds the state is NORMAL.
     */
    private SensorState state;

    public SensorValue(int sensorId, String sensorCode, Long timestamp,
                       double value, double minValue, double maxValue) {
        this.sensorId = sensorId;
        this.sensorCode = sensorCode;
        this.timestamp = timestamp;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public SensorValue(int sensorId, long time,
            double value) {
        this.sensorId = sensorId;
        this.timestamp = time;
        this.value = value;

    }

    public SensorValue(double value) {
        this.value = value;
    }

    public SensorValue(String sensorCode, long time,
                       double value) {
        this.sensorCode = sensorCode;
        this.timestamp = time;
        this.value = value;
    }

    public SensorValue(int sensorId, String sensorCode, long time,
                       double value) {
        this.sensorId = sensorId;
        this.sensorCode = sensorCode;
        this.timestamp = time;
        this.value = value;
    }

    public enum SensorState {

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
        OFF

    }
}
