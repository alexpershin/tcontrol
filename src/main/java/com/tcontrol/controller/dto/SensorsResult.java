package com.tcontrol.controller.dto;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Sensors wrapper to be passed to web UI.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "Объект с информацией о сенсорах")
public class SensorsResult {

    private SensorWeb[] sensors;
}
