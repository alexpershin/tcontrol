package org.tcontrol.config.props;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class RelayPinsConfig {
    private List<RelayPinConfig> pins = new ArrayList<>();

    @Data
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class RelayPinConfig {
        private int pinId;
        private String pin;
        private String name;
        private String userName;
    }
}
