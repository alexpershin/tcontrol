package org.tcontrol.config.props;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.service.models.Sensor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Slf4j
@NoArgsConstructor
public class SensorConfig {
    @Getter
    private List<SensorConfiguration> sensors = new ArrayList<>();
    private Map<String, SensorConfiguration> codeToSensorConfMap = new HashMap<>();
    private Map<Integer, SensorConfiguration> idToSensorConfMap = new HashMap<>();

    public SensorConfig(List<SensorConfiguration> sensors){
        setSensors(sensors);
    }

    public void setSensors(List<SensorConfiguration> sensors) {
        this.sensors = sensors;
        codeToSensorConfMap = sensors.stream().collect(Collectors.toMap(SensorConfiguration::getCode, s -> s));
        idToSensorConfMap = sensors.stream().collect(toMap(SensorConfiguration::getId, sc -> sc));
    }

    public Optional<SensorConfiguration> sensorConfigByCode(String code){
        return ofNullable(codeToSensorConfMap.get(code));
    }

    public Optional<SensorConfiguration> sensorConfigById(Integer id){
        return  ofNullable(idToSensorConfMap.get(id));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensorConfiguration {

        private int id;
        private String code;
        private String name;
        private Sensor.SensorType type;
        private String userName;
        private String description;
        /**
         * Lower threshold of value to change the sensor state.
         */
        private double lowThreshold;

        /**
         * Hight threshold of value to change the sensor state.
         */
        private double highThreshold;
        /**
         * TODO Rename SD - Switching differential Threshold delta. When value is
         * getting closer to a threshold the state can be set to pre alarm.
         */
        private double thresholdLag;

        public SensorConfiguration(int id, String code, String name,
                                   Sensor.SensorType type,
                                   String userName, String description) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.type = type;
            this.userName = userName;
            this.description = description;
        }
    }

}
