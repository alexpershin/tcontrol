package org.tcontrol.service;

import org.tcontrol.service.impl.Thermostat;

import java.util.List;

public interface IThermostat {

  boolean checkTemperature();

  boolean isHeatingOn();

  boolean isOn();

  float getMediumT();

  Thermostat on(Float temperature);

  float currentTemperature();

  Thermostat off();

  List<String> getHeatingPins();

  String getName();

  String getSensorCode();
}
