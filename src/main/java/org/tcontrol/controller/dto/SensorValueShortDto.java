package org.tcontrol.controller.dto;

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
public class SensorValueShortDto {

    private int sensorId;
    private long timestamp;
    private double value;
}
