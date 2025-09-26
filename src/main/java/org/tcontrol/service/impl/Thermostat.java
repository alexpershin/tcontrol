package org.tcontrol.service.impl;

import com.pi4j.io.gpio.PinState;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.tcontrol.config.props.ThermostatsConfig;
import org.tcontrol.hardware.ITemperatureMonitor;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.ITimer;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.lang.Float.max;
import static org.tcontrol.config.props.ThermostatsConfig.ThermostatConfig;

@RequiredArgsConstructor
@Slf4j
public class Thermostat implements IThermostat {
    public static final float TEMPERATURE_MAX_ON = 28.0f;

    @Autowired
    @Setter
    private ThermostatsConfig thermostatsConfig;

    @Autowired
    @Setter
    private ITemperatureMonitor temperatureMonitor;

    @Autowired
    @Setter
    private IRelayHandler relayHandler;

    @Autowired
    @Setter
    private ITimer timer;

    private ThermostatsConfig.ThermostatConfig thermostatCurrentConfig;

    private ThermostatsConfig.ThermostatConfig thermostatConfig;

    @Getter
    private final String name;

    @Getter
    boolean heatingOn;

    @Getter
    boolean on = true;

    @Getter
    float mediumT;

    private boolean firstTime = true;

    @PostConstruct
    public void init() {
        log.info("init");
        thermostatConfig = thermostatsConfig.getThermostats()
                .stream()
                .filter(tc -> tc.getName().equals(name))
                .findAny()
                .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Thermostat config not found by name: {0}", name)));
        thermostatCurrentConfig = thermostatConfig.clone();
    }

    @Override
    @Scheduled(cron = "${thermostat.schedule}")
    public boolean checkTemperature() {
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
        return heatingOn;
    }

    @Override
    public Thermostat on(Float temperature) {
        this.on = true;
        if (temperature != null) {
            ThermostatConfig clone;
            clone = thermostatCurrentConfig.clone();
            clone.setTNight(temperature);
            clone.setTDay(temperature);
            thermostatCurrentConfig = clone;
        }
        log.info("{}> thermostat is ON: {}", name, temperature);
        return this;
    }

    @Override
    public float currentTemperature() {
        return max(thermostatCurrentConfig.getTDay(), thermostatCurrentConfig.getTNight());
    }

    @Override
    public Thermostat off() {
        this.on = false;
        thermostatCurrentConfig = thermostatConfig.clone();
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
