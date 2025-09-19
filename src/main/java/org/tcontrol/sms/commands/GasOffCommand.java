package org.tcontrol.sms.commands;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.sms.ISMSCommand;
import org.tcontrol.service.IThermostat;

@Slf4j
@AllArgsConstructor
public class GasOffCommand implements ISMSCommand {

  private final IThermostat thermostatGas;

  @Override
  public CommandResult run() {

    thermostatGas.off();

    log.info("Executed");
    return new CommandResult(STATUS.OK, "Gas thermostat is off");
  }
}
