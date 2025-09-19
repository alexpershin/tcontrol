package org.tcontrol.sms.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

import static org.tcontrol.service.impl.Thermostat.TEMPERATURE_MAX_ON;

@Slf4j
@AllArgsConstructor
public class GasOnCommand implements ISMSCommand {

    private IThermostat thermostatGas;

    @Override
    public CommandResult run() {

        thermostatGas.on(TEMPERATURE_MAX_ON);

        log.info("Executed");
        return new CommandResult(STATUS.OK, "Gas thermostat is on");
    }
}
