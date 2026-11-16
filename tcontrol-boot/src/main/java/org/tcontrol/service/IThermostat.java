package org.tcontrol.service;

import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.service.impl.Thermostat;

import java.util.List;

public interface IThermostat {

  boolean checkTemperature();

  boolean isHeatingOn();

  boolean isChanged();

  float getMediumT();

  Thermostat temperature(Float temperature) ;

  float currentTemperature();

  Float sensorTemperature();

  Thermostat rollback();

  List<String> getHeatingPins();

  String getName();

  Integer getId();

  String  getCode();

  String getSensorCode();

  ThermostatsConfig.ThermostatConfig getThermostatConfig();

  ThermostatsConfig.ThermostatConfig getThermostatCurrentConfig();
}
