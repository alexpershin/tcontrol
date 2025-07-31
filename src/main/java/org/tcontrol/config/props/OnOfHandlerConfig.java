package org.tcontrol.config.props;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

@Data
@RequiredArgsConstructor
public class OnOfHandlerConfig {
  private int pinId;
  private int sensorId;
}
