package com.tcontrol.controller.dto;


import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import com.tcontrol.dao.Sensor;

/**
 * Sensor to be passed to web UI. 
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@Schema(description = "Объект с информацией о сенсоре")
public class SensorWeb {

    @Schema(example = "1", description = "Идентификатор сенсора", maxLength = 4)
    private int id;

    private Sensor.SensorType type;

    private String name;
    
    private String description;

    /**
     * Constructs the sensors.
     */
    public SensorWeb() {
    }

    /**
     * Constructs the sensor.
     *
     * @param name sensor's name;
     * @param id sensor's id;
     */
    public SensorWeb(String name, int id, Sensor.SensorType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

}
