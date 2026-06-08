package org.tcontrol.config.props;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@FieldNameConstants
public class ThermostatsConfig {

    @Getter
    private List<ThermostatConfig> thermostats;
    private Map<Integer, ThermostatConfig> nameToThermostatConfigMap = new HashMap<>();

    public void setThermostats(List<ThermostatConfig> thermostatConfigs) {
        this.thermostats = thermostatConfigs;
        this.nameToThermostatConfigMap =
                thermostatConfigs.stream().collect(Collectors.toMap(ThermostatConfig::getId, tc -> tc));
    }

    public Optional<ThermostatConfig> thermostatConfigById(Integer id){
        return ofNullable(nameToThermostatConfigMap.get(id));
    }

    public Set<Integer> thermostatIds(){
        return nameToThermostatConfigMap.keySet();
    }

    @Data
    @RequiredArgsConstructor
    @FieldNameConstants
    public static class ThermostatConfig implements Cloneable {

        private Integer id;
        private String schedule;
        private String code;
        private String name;
        private float tDay;
        private float tNight;
        private float delta;
        private int nightBegin;
        private int nightEnd;
        private String sensor;
        private List<String> relayPins;

        @Override
        public ThermostatConfig clone() {
            try {
                ThermostatConfig clone = (ThermostatConfig) super.clone();
                clone.setRelayPins(relayPins.stream().map(String::new).collect(Collectors.toList()));
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
