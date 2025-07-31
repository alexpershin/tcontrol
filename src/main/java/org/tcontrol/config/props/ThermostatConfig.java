package org.tcontrol.config.props;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class ThermostatConfig {

  private float tDay;
  private float tNight;
  private float delta;
  private int nightBegin;
  private int nightEnd;
  private String sensor;
  private List<String> relayPins =  new ArrayList<>();

  @PostConstruct
  public void postConstruct() {
    nightBegin();
    nightEnd();
    relayPins.forEach(RaspiPin::getPinByName);
  }

  public LocalTime nightBegin() {
    return LocalTime.ofSecondOfDay(nightBegin);
  }

  public LocalTime nightEnd() {
    return LocalTime.ofSecondOfDay(nightEnd);
  }

  public LinkedList<Pin> relayPins() {
    return relayPins.stream().map(RaspiPin::getPinByName).collect(Collectors.toCollection(LinkedList::new));
  }
}
