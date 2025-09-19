package org.tcontrol.sms.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

import static org.tcontrol.service.impl.Thermostat.TEMPERATURE_MAX_ON;

@Slf4j
@RequiredArgsConstructor
public class HeatingOnCommand implements ISMSCommand {

    private final IThermostat thermostatElectro;

    @Override
    public CommandResult run() {
        thermostatElectro.on(TEMPERATURE_MAX_ON);

        log.info("Executed");
        return new CommandResult(STATUS.OK, "heating is on");
    }
}
