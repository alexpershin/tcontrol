package org.tcontrol.sms.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

@Component
@Slf4j
@Qualifier("thermostatOnCommand")
@AllArgsConstructor
public class ThermostatOnCommand implements ISMSCommand {

    private IThermostat thermostatElectro;

    @Override
    public CommandResult run() {

        thermostatElectro.changeOn(true);

        log.info("Executed");
        return new CommandResult(STATUS.OK, "Termostat is on");
    }
}
