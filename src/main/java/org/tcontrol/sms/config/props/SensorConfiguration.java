package org.tcontrol.sms.config.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tcontrol.service.models.Sensor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorConfiguration
{
    private int id;
    private String code;
    private String name;
    private Sensor.SensorType type;
    private String userName;
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

    public SensorConfiguration(int id, String code, String name,
                               Sensor.SensorType type,
                               String userName, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.userName = userName;
        this.description = description;
    }
}
