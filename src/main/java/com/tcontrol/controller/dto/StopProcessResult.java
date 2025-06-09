package com.tcontrol.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Start process wrapper to be passed to web UI.
 */
@Getter
@Setter
@AllArgsConstructor
public class StopProcessResult {
    private Integer value;
    private String status;
}
