package org.tcontrol.sms.config.props;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class VoltageControllerConfig {

  private float voltageMin;
  private float voltageMax;
  private Integer valueId;
  private List<String> relayPins =  new ArrayList<>();

  @PostConstruct
  public void postConstruct() {
    relayPins.forEach(RaspiPin::getPinByName);
  }

  public List<Pin> relayPins() {
    return relayPins.stream().map(RaspiPin::getPinByName).collect(Collectors.toList());
  }
}
