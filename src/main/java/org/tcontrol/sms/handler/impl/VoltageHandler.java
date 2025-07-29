package org.tcontrol.sms.handler.impl;

import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.sms.handler.IRelayHandler;
import org.tcontrol.sms.IVoltageMonitor;
import org.tcontrol.sms.config.props.VoltageControllerConfig;
import org.tcontrol.sms.handler.IVoltageHandler;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoltageHandler implements IVoltageHandler {

  private final VoltageControllerConfig voltageControllerConfig;

  private final IVoltageMonitor voltageMonitor;

  private final IRelayHandler relayHandler;

  @Getter
  private String name;

  @Getter
  boolean enable = true;

  @Getter
  boolean on = false;

  @PostConstruct
  public void init() {
    this.name = "Battery voltage controller";
    log.info("Initializing voltage controller: {}", name);
  }

  @Override
  @Scheduled(cron = "${voltage-controller.schedule}")
  public void checkVoltage() {
    log.info(name + "> checkVoltage");
    if (!enable) {
      return;
    }
    List<IVoltageMonitor.VoltageResult> voltageResults = voltageMonitor.getVoltageResults();
    if (voltageResults != null && !voltageResults.isEmpty()) {
      IVoltageMonitor.VoltageResult value
            = voltageResults.get(voltageControllerConfig.getValueId());

      double currentVoltage =  value.getValue();

      if (on && currentVoltage <= voltageControllerConfig.getVoltageMin()) {
        on = false;
      } else if(!on && currentVoltage >= voltageControllerConfig.getVoltageMax()){
        on = true;
      }

      voltageControllerConfig.relayPins().forEach(heatingPin -> {
            PinState oldState = relayHandler.getPinState(heatingPin);
            if (oldState == null || oldState.isHigh() != on) {
              if (on) {
                relayHandler.turnOnRelay(heatingPin);
              } else {
                relayHandler.turnOffRelay(heatingPin);
              }
            }
          });

      log.info(name + "> Checked voltage between({},{}), current:{}, state:{}",
              voltageControllerConfig.getVoltageMin(),
              voltageControllerConfig.getVoltageMax(),
              currentVoltage, on);
    } else {
      log.error("Voltage values is null");
    }
  }
}
