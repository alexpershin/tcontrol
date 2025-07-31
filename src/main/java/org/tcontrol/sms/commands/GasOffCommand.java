package org.tcontrol.sms.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;
import org.tcontrol.config.props.ThermostatConfig;

@Component
@Slf4j
@Qualifier("thermostatOffCommand")
@AllArgsConstructor
public class GasOffCommand implements ISMSCommand {

  private final IRelayHandler relayHandler;

  private final IThermostat thermostatGas;

  private final ThermostatConfig thermostatGasConfig;

  @Override
  public CommandResult run() {

    thermostatGas.changeOn(false);
    thermostatGasConfig.relayPins().forEach(pin -> {
      relayHandler.turnOffRelay(pin);
    });

    log.info("Executed");
    return new CommandResult(STATUS.OK, "Gas thermostat is off");
  }
}
