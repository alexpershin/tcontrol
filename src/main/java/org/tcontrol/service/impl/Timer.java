package org.tcontrol.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.tcontrol.service.ITimer;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class Timer implements ITimer {
    @Override
    public LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }

    @Override
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
}
