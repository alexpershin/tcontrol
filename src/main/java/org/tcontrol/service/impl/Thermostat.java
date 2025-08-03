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

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class Thermostat implements IThermostat {
    public static final float TEMPERATURE_MAX_ON = 28.0f;

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
    private ThermostatConfig thermostatCurrentConfig;

    @PostConstruct
    public void postConstruct() {
        thermostatCurrentConfig = thermostatConfig.toBuilder().build();
    }

    @Override
    @Scheduled(cron = "${thermostat.schedule}")
    public void checkTemperature() {
        log.info(name + "> checkTemperature");

        mediumT = night() ? thermostatCurrentConfig.getTNight() : thermostatCurrentConfig.getTDay();

        SensorValue sensorValue = temperatureMonitor.getSensorValueMap()
                .get(thermostatCurrentConfig.getSensor());
        if (sensorValue != null) {

            float currentT = (float) sensorValue.getValue();

            float delta = thermostatCurrentConfig.getDelta();

            if (heatingOn) {
                heatingOn = currentT <= mediumT + delta;
            } else {
                heatingOn = currentT <= mediumT - delta;
            }

            thermostatCurrentConfig.relayPins().forEach(heatingPin -> {
                PinState oldState = relayHandler.getPinState(heatingPin);
                if (oldState == null || oldState.isHigh() != heatingOn) {
                    if (heatingOn) {
                        relayHandler.turnOnRelay(heatingPin);
                    } else {
                        relayHandler.turnOffRelay(heatingPin);
                    }
                }
            });

            log.info("{}> Check temperature between({},{}), current:{}, state:{}", name, mediumT - delta, mediumT + delta, currentT, heatingOn);
        } else {
            if (firstTime) log.info("no sensor value yet");
            else log.error("temperature sensor value is null");
        }
        firstTime = false;
    }

    @Override
    public Thermostat on(Float temperature) {
        this.on = true;
        if (temperature != null) {
            thermostatCurrentConfig.setTDay(temperature);
            thermostatCurrentConfig.setTNight(temperature);
        }
        log.info("{}> thermostat is ON: {}", name, temperature);
        return this;
    }

    @Override
    public Thermostat off() {
        this.on = false;
        thermostatCurrentConfig = thermostatConfig;
        log.info("{}> thermostat is back OFF", name);
        return this;
    }

    @Override
    public List<String> getHeatingPins() {
        return thermostatCurrentConfig.getRelayPins();
    }

    @Override
    public String getSensorCode() {
        return thermostatCurrentConfig.getSensor();
    }

    private boolean night() {

        LocalDateTime currentLocalTime = timer.getCurrentTime();
        LocalDate currentDate = currentLocalTime.toLocalDate();
        LocalTime currentTme = currentLocalTime.toLocalTime();

        LocalTime nightBegin = thermostatCurrentConfig.nightBegin();
        LocalDateTime begin = LocalDateTime.of(currentDate, nightBegin);

        LocalTime nightEnd = thermostatCurrentConfig.nightEnd();
        LocalDateTime end = LocalDateTime.of(currentDate, nightEnd);

        if (thermostatCurrentConfig.getNightBegin() > thermostatCurrentConfig.getNightEnd()) {
            if (currentTme.isAfter(nightBegin)) {
                end = end.plusDays(1);
            } else if (currentTme.isBefore(nightBegin)) {
                begin = begin.minusDays(1);
            }
        }

        return currentLocalTime.isAfter(begin) && currentLocalTime.isBefore(end);
    }

}
