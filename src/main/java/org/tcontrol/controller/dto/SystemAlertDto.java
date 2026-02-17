package org.tcontrol.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SystemAlertDto {
    long timestamp;
    String message;
}
