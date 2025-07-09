package org.tcontrol.sms.commands;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.tcontrol.sms.*;
import org.tcontrol.sms.IVoltageMonitor.VoltageResult;
import org.tcontrol.sms.config.props.SensorConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


@Component
@Slf4j
@Qualifier("statusCommand")
@AllArgsConstructor
public class StatusCommand implements ISMSCommand {

  private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
      "yyyy.MM.dd HH:mm:ss");

  private final ITemperatureMonitor temperatureMonitor;

  private final SensorConfig sensorConfig;

  private final IRelayController relayController;

  private final IThermostat thermostatElectro;

  private final IThermostat thermostatGas;

  private final IThermostat thermostatVeranda;

  private final IThermostat thermostatMyRoom;

  private final IVoltageMonitor voltageMonitor;

  @Override
  public CommandResult run() {
    Optional<String> res = temperatureMonitor.getSensorValueMap().values().stream().map(value -> {
      String sensorCode = value.getSensorCode();
      Optional<SensorConfig.SensorConfiguration> sc = sensorConfig.getSensors().stream()
          .filter(sensor -> sensor.getCode().equals(sensorCode)).findFirst();
      return sc.map(sensorConfiguration -> sensorConfiguration.getName() + ": " + value.getValue())
          .orElse("");
    }).reduce((a, b) -> a + "\n" + b);

    res = thermostatStatus(res, thermostatElectro, relayController);
    res = thermostatStatus(res, thermostatGas, relayController);
    res = thermostatStatus(res, thermostatVeranda, relayController);
    res = thermostatStatus(res, thermostatMyRoom, relayController);
    res = voltageStatus(res, voltageMonitor);

    res = Optional.of(res.orElse("") + "\n" + simpleDateFormat.format(new Date()));

    String text = res.orElse("status not ready");
    log.info("Executed");
    return new CommandResult(res.isPresent() ? STATUS.OK : STATUS.FAILURE, text);
  }

  private Optional<String> voltageStatus(Optional<String> res, IVoltageMonitor voltageMonitor) {
    for (VoltageResult vr : voltageMonitor.getVoltageResults()) {
      res = Optional.of(
          res.orElse("")
              + "\n" + vr.getName() + ": " + vr.getValue() + vr.getUnit());
    }
    return res;
  }

  private static Optional<String> thermostatStatus(Optional<String> res,
      IThermostat thermostat, IRelayController relayController) {

    StringBuilder resultString = new StringBuilder(" ");
    for (String heatingPin : thermostat.getHeatingPins()) {
      Pin pin = RaspiPin.getPinByName(heatingPin);
      PinState heatingState = relayController.getPinState(pin);
      resultString.append(heatingState == null ? "UNDEFINED" : (heatingState.isHigh() ? "ON" : "OFF"));
    }

    res = Optional.of(
        res.orElse("")
            + "\n" + thermostat.getName() + "(" + thermostat.getMediumT() + "): "
            + (thermostat.isOn() ? "ON" : "OFF") + ", heating:" + resultString);
    return res;
  }
}
