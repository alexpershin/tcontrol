package org.tcontrol.service;

import java.util.List;

public interface IThermostat {

  void checkTemperature();

  boolean isHeatingOn();

  boolean isOn();

  float getMediumT();

  void changeOn(boolean v);

  List<String> getHeatingPins();

  String getName();
}
