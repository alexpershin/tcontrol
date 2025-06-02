package com.tcontrol.controller.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Sensor values wrapper to be passed to web UI.
 */
@Getter
@Setter
public class SensorValuesResult {

    private SensorValueWeb[] values;
}
