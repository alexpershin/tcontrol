package org.tcontrol.controller.dto;


import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import org.tcontrol.dao.Sensor;

/**
 * Sensor to be passed to web UI. 
 */
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект с информацией о сенсоре")
public class SensorDto {

    @Schema(example = "1", description = "Идентификатор сенсора", maxLength = 4)
    private int id;

    private Sensor.SensorType type;

    private String name;
    
    private String description;

    /**
     * Constructs the sensor.
     *
     * @param name sensor's name;
     * @param id sensor's id;
     */
    public SensorDto(String name, int id, Sensor.SensorType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }
}
