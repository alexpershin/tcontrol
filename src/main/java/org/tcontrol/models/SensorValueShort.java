package org.tcontrol.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorValueShort {

    private int sensorId;
    private String sensorCode;
    private long timestamp;
    private double value;
}
