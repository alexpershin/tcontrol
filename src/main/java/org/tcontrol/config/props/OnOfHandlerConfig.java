package org.tcontrol.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OnOfHandlerConfig {
  private int pinId;
  private int sensorId;
  private String thermostatCode;
}
