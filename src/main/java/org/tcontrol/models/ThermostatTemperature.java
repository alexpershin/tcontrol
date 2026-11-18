package org.tcontrol.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ThermostatTemperature {
    private String thermostatCode;
    private String thermostatName;
    private Float temperature;
    private Float dayTemperature;
    private Float nightTemperature;
    private LocalTime nightTime;
    private LocalTime dayTime;
}
