package org.tcontrol.controller.dto;

import lombok.Data;

@Data
public class ThermostatTemperatureDto {
    private String thermostatName;
    private Float temperature;
}
