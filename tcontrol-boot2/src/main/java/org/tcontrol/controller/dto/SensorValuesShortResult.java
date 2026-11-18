package org.tcontrol.controller.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Sensor values wrapper to be passed to web UI.
 */
@Getter
@Setter
public class SensorValuesShortResult {

    private SensorValueShortDto[] values;
    private long time;
}
