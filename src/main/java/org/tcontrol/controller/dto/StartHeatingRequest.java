package org.tcontrol.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Start process wrapper to be passed to web UI.
 */
@Getter
@Setter
@AllArgsConstructor
public class StartHeatingRequest {
    private int sensorId;
    private List<ThermostatTemperatureDto> newTemperatures;
    private SensorValueDto.SensorValueState state;
}
