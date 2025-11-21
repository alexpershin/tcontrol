package org.tcontrol.sms.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

@Slf4j
@RequiredArgsConstructor
public class ThermostatOffCommand implements ISMSCommand {

    private final IThermostat thermostatElectro;

    @Override
    public CommandResult run() {

        thermostatElectro.rollback();

        log.info("Executed");
        return new CommandResult(STATUS.OK, "Termostat is off");
    }
}
