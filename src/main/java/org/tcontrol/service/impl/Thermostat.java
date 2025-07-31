package org.tcontrol.service.impl;

import com.pi4j.io.gpio.PinState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.config.props.ThermostatConfig;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class Thermostat implements IThermostat {

    private final ThermostatConfig thermostatConfig;

    private final ITemperatureMonitor temperatureMonitor;

    private final IRelayHandler relayHandler;

    private final ITimer timer;

    @Getter
    private final String name;

    @Getter
    boolean heatingOn;

    @Getter
    boolean on = true;

    @Getter
    float mediumT;

    private boolean firstTime = true;

    @Override
    @Scheduled(cron = "${thermostat.schedule}")
    public void checkTemperature() {
        log.info(name + "> checkTemperature");
        if (!on) {
            return;
        }

        mediumT = night() ? thermostatConfig.getTNight() : thermostatConfig.getTDay();

        SensorValue sensorValue = temperatureMonitor.getSensorValueMap()
                .get(thermostatConfig.getSensor());
        if (sensorValue != null) {

            float currentT = (float) sensorValue.getValue();

            float delta = thermostatConfig.getDelta();

            if (heatingOn) {
                heatingOn = currentT <= mediumT + delta;
            } else {
                heatingOn = currentT <= mediumT - delta;
            }

            thermostatConfig.relayPins().forEach(heatingPin -> {
                PinState oldState = relayHandler.getPinState(heatingPin);
                if (oldState == null || oldState.isHigh() != heatingOn) {
                    if (heatingOn) {
                        relayHandler.turnOnRelay(heatingPin);
                    } else {
                        relayHandler.turnOffRelay(heatingPin);
                    }
                }
            });

            log.info(name + "> Check temperature between({},{}), current:{}, state:{}", mediumT - delta,
                    mediumT + delta,
                    currentT, heatingOn);
        } else {
            if (firstTime) log.info("no sensor value yet");
            else log.error("temperature sensor value is null");
        }
        firstTime = false;
    }

    @Override
    public void changeOn(boolean v) {
        if (!v && on) {
            this.on = false;
            thermostatConfig.relayPins().forEach(relayHandler::turnOffRelay);
            log.info(name + "> termostat's relay switched off");
        } else {
            this.on = v;
        }
        log.info(name + "> termostat is " + (this.on ? "ON" : "OFF"));
    }

    @Override
    public List<String> getHeatingPins() {
        return thermostatConfig.getRelayPins();
    }

    private boolean night() {

        LocalDateTime currentLocalTime = timer.getCurrentTime();
        LocalDate currentDate = currentLocalTime.toLocalDate();
        LocalTime currentTme = currentLocalTime.toLocalTime();

        LocalTime nightBegin = thermostatConfig.nightBegin();
        LocalDateTime begin = LocalDateTime.of(currentDate, nightBegin);

        LocalTime nightEnd = thermostatConfig.nightEnd();
        LocalDateTime end = LocalDateTime.of(currentDate, nightEnd);

        if (thermostatConfig.getNightBegin() > thermostatConfig.getNightEnd()) {
            if (currentTme.isAfter(nightBegin)) {
                end = end.plusDays(1);
            } else if (currentTme.isBefore(nightBegin)) {
                begin = begin.minusDays(1);
            }
        }

        return currentLocalTime.isAfter(begin) && currentLocalTime.isBefore(end);
    }

}
