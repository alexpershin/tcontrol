package org.tcontrol.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public final class SystemAlert {
    @Setter
    long lastTimestamp;
    final String message;
    int count;

    public void increment(){
        count++;
    }
}
