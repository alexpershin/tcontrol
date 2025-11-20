package org.tcontrol.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThermostatTemperature {
    private String thermostatCode;
    private String thermostatName;
    private Float temperature;
}
