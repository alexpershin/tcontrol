package org.tcontrol.config.props;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
public class RelayPinsConfig {
    private Map<Integer, RelayPinConfiguration>  idToRelayPinConfigMap = new HashMap<>();
    private Map<String, RelayPinConfiguration> pinToRelayPinConfigMap = new HashMap<>();

    public void setPins(List<RelayPinConfiguration> pins) {
        idToRelayPinConfigMap = pins.stream().collect(Collectors.toMap(RelayPinConfiguration::getPinId, p->p));
        pinToRelayPinConfigMap = pins.stream().collect(Collectors.toMap(RelayPinConfiguration::getPin, p->p));
    }

    public Optional<RelayPinConfiguration> pinConfigurationById(Integer id){
        return ofNullable(idToRelayPinConfigMap.get(id));
    }

    public Optional<RelayPinConfiguration> pinConfigurationByPin(String pin){
        return ofNullable(pinToRelayPinConfigMap.get(pin));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelayPinConfiguration {
        private int pinId;
        private String pin;
        private String name;
        private String userName;
    }
}
