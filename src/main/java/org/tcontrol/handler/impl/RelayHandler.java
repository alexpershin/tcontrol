package org.tcontrol.handler.impl;

import com.pi4j.io.gpio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tcontrol.service.IPinFactory;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.models.SensorValue;
import org.tcontrol.service.impl.CSVStatisticSaver;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.config.props.RelayPinsConfig;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelayHandler implements IRelayHandler {

    private final CSVStatisticSaver csvStatisticSaver;

    private final IStatisticsSaver sensorLastDataSaver;

    private final IPinFactory pinFactory;

    private final RelayPinsConfig relayPinsConfig;

    @PostConstruct
    void init() {
        log.info("Initializing relay controller...");
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


        log.info("{} state was: {} ", pin.getName(), pin.getState());

        SensorValue sensorValue = new SensorValue();
        Optional<RelayPinsConfig.RelayPinConfig> relayPinConfigOptional =
                relayPinsConfig.getPins().stream()
                        .filter(p -> p.getPin().equals(controlPin.getName()))
                        .findFirst();
        if (relayPinConfigOptional.isPresent()) {
            RelayPinsConfig.RelayPinConfig relayPinConfig = relayPinConfigOptional.get();
            sensorValue.setSensorId(relayPinConfig.getPinId());
            sensorValue.setSensorCode(relayPinConfig.getName());
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
            csvStatisticSaver.write(values);
            sensorLastDataSaver.write(values);

            log.info("Now {} state is: {} ", pin.getName(), pin.getState());
            return pin.getState();
        } else {
            log.error("Configuration for pin: {} not found!", controlPin.getName());
            return null;
        }
    }

}
