package org.tcontrol.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SystemAlertDto {
    long lastTimestamp;
    String message;
    int count;
}
