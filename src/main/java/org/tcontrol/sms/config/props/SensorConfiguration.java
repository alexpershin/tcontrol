package org.tcontrol.sms.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.tcontrol.service.models.Sensor;

@Data
@RequiredArgsConstructor
public class SensorConfiguration
{
    private int id;
    private String code;
    private String name;
    private Sensor.SensorType type;
    private String description;
    /**
     * Lower threshold of value to change the sensor state.
     */
    private double lowThreshold;

    /**
     * Hight threshold of value to change the sensor state.
     */
    private double highThreshold;
    /**
     * TODO Rename SD - Switching differential Threshold delta. When value is
     * getting closer to a threshold the state can be set to pre alarm.
     */
    private double thresholdLag;

    public SensorConfiguration(int id, String code, String name, Sensor.SensorType type, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public SensorConfiguration(int id, String code, String name,
                               Sensor.SensorType type, String description,
                               double lowThreshold, double highThreshold, double thresholdLag) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.description = description;
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.thresholdLag = thresholdLag;
    }
}
