package org.tcontrol.sms.config.props;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.tcontrol.service.models.Sensor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class SensorConfig {
    private List<SensorConfiguration> sensors = new ArrayList<>();

    @Data
    @RequiredArgsConstructor
    public static class SensorConfiguration
    {
        private String code;
        private int id;
        private String name;
        private Sensor.SensorType type;
        private String description;
    }
}
