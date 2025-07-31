package org.tcontrol.sms.config.props;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

@Data
@RequiredArgsConstructor
public class ButtonHandlerConfig {

  private String buttonPinId;
  private String buttonPinName;

  @PostConstruct
  public void postConstruct() {
    if (pin() == null) {
      throw new IllegalArgumentException("pin '" + buttonPinId + "' doesn't exist");
    }
  }

  public Pin pin() {
    return RaspiPin.getPinByName(buttonPinId);
  }
}
