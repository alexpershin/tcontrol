package org.tcontrol.controller.dto;

import lombok.Data;

@Data
public class ThermostatTemperatureDto {
    private String thermostatCode;
    private String thermostatName;
    private Float temperature;
    private Float dayTemperature;
    private Float nightTemperature;
    private String nightTime;
    private String dayTime;
}
