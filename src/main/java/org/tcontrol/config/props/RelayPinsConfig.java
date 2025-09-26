package org.tcontrol.config.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@RequiredArgsConstructor
public class RelayPinsConfig {
    private List<RelayPinConfiguration> pins = new ArrayList<>();

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
