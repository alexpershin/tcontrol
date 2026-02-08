package org.tcontrol.models;

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
public class SensorValue extends SensorValueShort {

    private int sensorValueId;
    private String pin;
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
        super(sensorId, sensorCode, timestamp, value);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public SensorValue(int sensorId, long timestamp,
            double value) {
        super(sensorId, null, timestamp, value);
    }

    public SensorValue(String sensorCode, long timestamp,
                       double value) {
        super.setSensorCode(sensorCode);
        super.setTimestamp(timestamp);
        super.setValue(value);
    }

    public SensorValue(int sensorId, String sensorCode, long timestamp,
                       double value) {
        super(sensorId, sensorCode, timestamp, value);
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
