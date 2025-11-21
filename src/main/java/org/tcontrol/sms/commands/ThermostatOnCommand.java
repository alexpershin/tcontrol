package org.tcontrol.sms.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

import java.util.Map;

import static org.tcontrol.service.impl.Thermostat.TEMPERATURE_MAX_ON;

@Slf4j
@AllArgsConstructor
public class ThermostatOnCommand implements ISMSCommand {

    private final Map<String, IThermostat> dynamicThermostats;

    @Override
    public CommandResult run() {

        IThermostat thermostatElectro = dynamicThermostats.get("thermostat-electro");
        thermostatElectro.temperature(TEMPERATURE_MAX_ON);

        log.info("Executed");
        return new CommandResult(STATUS.OK, "Thermostat is on");
    }
}
