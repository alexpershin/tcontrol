package org.tcontrol.sms;

import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tcontrol.service.IPinFactory;
import org.tcontrol.sms.config.props.VoltageControllerConfig;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoltageController implements IVoltageController {

  private final VoltageControllerConfig voltageControllerConfig;

  private final IVoltageMonitor voltageMonitor;

  private final IRelayController relayController;

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
    if (voltageResults != null) {
      IVoltageMonitor.VoltageResult value
            = voltageResults.get(voltageControllerConfig.getValueId());

      double currentVoltage =  value.getValue();

      if (on && currentVoltage <= voltageControllerConfig.getVoltageMin()) {
        on = false;
      } else if(!on && currentVoltage >= voltageControllerConfig.getVoltageMax()){
        on = true;
      }

      voltageControllerConfig.relayPins().forEach(heatingPin -> {
            PinState oldState = relayController.getPinState(heatingPin);
            if (oldState == null || oldState.isHigh() != on) {
              if (on) {
                relayController.turnOnRelay(heatingPin);
              } else {
                relayController.turnOffRelay(heatingPin);
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
