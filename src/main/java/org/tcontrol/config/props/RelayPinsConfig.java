package org.tcontrol.config.props;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@RequiredArgsConstructor
public class RelayPinsConfig {
    private List<RelayPinConfiguration> pins = new ArrayList<>();
    private Map<Integer, RelayPinConfiguration>  idToRelayPinConfigMap = new HashMap<>();
    private Map<String, RelayPinConfiguration>  nameToRelayPinConfigMap = new HashMap<>();

    public void setPins(List<RelayPinConfiguration> pins) {
        this.pins = pins;
        idToRelayPinConfigMap = pins.stream().collect(Collectors.toMap(RelayPinConfiguration::getPinId, p->p));
        nameToRelayPinConfigMap = pins.stream().collect(Collectors.toMap(RelayPinConfiguration::getName, p->p));
    }

    @PostConstruct
    public void init() {
        log.info("init");
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
