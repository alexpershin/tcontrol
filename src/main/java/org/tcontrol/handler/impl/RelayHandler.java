package org.tcontrol.handler.impl;

import com.pi4j.io.gpio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.hardware.IPinFactory;
import org.tcontrol.models.SensorValue;
import org.tcontrol.service.IStatisticsManager;
import org.tcontrol.service.impl.CSVStatisticSaver;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.config.props.RelayPinsConfig;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.tcontrol.config.props.SensorConfig.SensorConfiguration;

@Slf4j
@RequiredArgsConstructor
@Logging
public class RelayHandler implements IRelayHandler {

    private final CSVStatisticSaver csvStatisticSaver;

    private final IStatisticsManager statisticsManager;

    private final IPinFactory pinFactory;

    private final RelayPinsConfig relayPinsConfig;

    private final SensorConfig sensorConfig;

    @PostConstruct
    void init() {
        log.info("Initializing relay handler...");
    }

    @Override
    public PinState turnOnRelay(Pin controlPin) {
        return changeStateOfRelay(PinState.HIGH, controlPin);
    }

    @Override
    public PinState turnOffRelay(Pin controlPin) {
        return changeStateOfRelay(PinState.LOW, controlPin);
    }

    @Override
    public PinState getPinState(Pin controlPin) {

        GpioPinDigitalOutput gpioPinDigitalOutput = pinFactory.getDigitalPin(controlPin);

        return gpioPinDigitalOutput != null ? gpioPinDigitalOutput.getState() : null;
    }

    private PinState changeStateOfRelay(PinState pinState, Pin controlPin) {

        GpioPinDigitalOutput pin = pinFactory.getDigitalPin(controlPin);

        if (pin == null) {
            return null;
        }

        // set shutdown state for this pin
        pin.setShutdownOptions(true, pinState);


        log.info("{}:{} state was: {} ", pin.getName(), controlPin.getName(), pin.getState());

        SensorValue sensorValue = new SensorValue();
        Optional<RelayPinsConfig.RelayPinConfiguration> relayPinConfigOptional =
                relayPinsConfig.pinConfigurationByPin(controlPin.getName());

        if (relayPinConfigOptional.isPresent()) {
            RelayPinsConfig.RelayPinConfiguration relayPinConfiguration = relayPinConfigOptional.get();

            sensorValue.setSensorId(
                    sensorConfig.sensorConfigByCode(String.valueOf(relayPinConfiguration.getPinId()))
                    .map(SensorConfiguration::getId)
                    .orElseGet(relayPinConfiguration::getPinId)
            );

            sensorValue.setSensorCode(String.valueOf(relayPinConfiguration.getPinId()));
            sensorValue.setPin(controlPin.getName());
            sensorValue.setTimestamp(System.currentTimeMillis());

            // turn off/heatingOn gpio pin #17
            if (pinState.isLow()) {
                pin.low();
                sensorValue.setValue(0.0);
            } else if (pinState.isHigh()) {
                pin.high();
                sensorValue.setValue(1.0);
            }

            List<SensorValue> values = Collections.singletonList(sensorValue);
            statisticsManager.write(values);

            log.info("Now {}:{} state is: {} ", pin.getName(), controlPin.getName(), pin.getState());
            return pin.getState();
        } else {
            log.error("Configuration for pin {}:{} not found!", controlPin.getName(), controlPin.getName());
            return null;
        }
    }

}
