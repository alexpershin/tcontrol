package org.tcontrol.service;

import org.tcontrol.service.impl.Thermostat;

import java.util.List;

public interface IThermostat {

  void checkTemperature();

  boolean isHeatingOn();

  boolean isOn();

  float getMediumT();

  Thermostat on(Float temperature);

  Thermostat off();

  List<String> getHeatingPins();

  String getName();

  String getSensorCode();
}
