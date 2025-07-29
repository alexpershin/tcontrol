package org.tcontrol.sms.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.handler.IRelayHandler;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.sms.IThermostat;
import org.tcontrol.sms.config.props.ThermostatConfig;

@Component
@Slf4j
@Qualifier("thermostatOffCommand")
@RequiredArgsConstructor
public class ThermostatOffCommand implements ISMSCommand {

    private final IRelayHandler relayHandler;

    private final IThermostat thermostatElectro;

    private final ThermostatConfig thermostatElectroConfig;

    @Override
    public CommandResult run() {

        thermostatElectro.changeOn(false);

        thermostatElectroConfig.relayPins().forEach(pin -> {
            relayHandler.turnOffRelay(pin);
        });

        log.info("Executed");
        return new CommandResult(STATUS.OK, "Termostat is off");
    }
}
