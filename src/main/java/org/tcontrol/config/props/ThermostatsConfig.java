package org.tcontrol.config.props;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.*;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThermostatsConfig {

    private List<org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig> thermostats = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ThermostatConfig implements Cloneable {

        private String name;
        private float tDay;
        private float tNight;
        private float delta;
        private int nightBegin;
        private int nightEnd;
        private String sensor;
        private List<String> relayPins;

        @Override
        public  ThermostatConfig clone()  {
            try {
                ThermostatConfig clone = (ThermostatConfig)super.clone();
                clone.setRelayPins(relayPins.stream()
                        .map(String::new)
                        .collect(Collectors.toList()));
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new UnsupportedOperationException("Clone not supported yet.");
            }
        }

        @PostConstruct
        public void init() {
            nightBegin();
            nightEnd();
            if (relayPins != null) {
                relayPins.forEach(RaspiPin::getPinByName);
            }
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
}
