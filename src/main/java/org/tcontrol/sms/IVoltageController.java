package org.tcontrol.sms;

import java.util.List;

public interface IVoltageController {

  void checkVoltage();

  List<String> getRelayPins();

  boolean isOn();
}
