package org.tcontrol.sms;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class Timer implements ITimer {
    @Override
    public LocalDateTime getCurrentTime(){
        return LocalDateTime.now();
    }
}
