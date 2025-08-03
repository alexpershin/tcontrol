package org.tcontrol.sms.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

@Component
@Slf4j
@Qualifier("heatingOffCommand")
@RequiredArgsConstructor
public class HeatingOffCommand implements ISMSCommand {
    private final IThermostat thermostatElectro;

    @Override
    public CommandResult run() {

        thermostatElectro.off();
        log.info("Executed");
        return new CommandResult(STATUS.OK,"heating is on");
    }
}
