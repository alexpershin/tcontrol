package org.tcontrol.sms;

import com.pi4j.io.gpio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tcontrol.service.IPinFactory;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.models.SensorValue;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;

@Component
@Slf4j
@RequiredArgsConstructor
public class RelayController implements IRelayController {

    private final CSVStatisticSaver csvStatisticSaver;

    private final IStatisticsSaver sensorLastDataSaver;

    private final IPinFactory pinFactory;

    @PostConstruct
    void init() {
        log.info("Initializing relay controller...");
    }

    @Override
    public PinState turnOnRelay(Pin controlPin) {
        return changeStateOfRelay(true, controlPin);
    }

    @Override
    public PinState turnOffRelay(Pin controlPin) {
        return changeStateOfRelay(false, controlPin);
    }

    @Override
    public PinState getPinState(Pin controlPin) {

        GpioPinDigitalOutput gpioPinDigitalOutput = pinFactory.getDigitalPin(controlPin);

        return gpioPinDigitalOutput!=null?gpioPinDigitalOutput.getState():null;
    }

    private PinState changeStateOfRelay(boolean action, Pin controlPin) {
        PinState pinState = action ? PinState.HIGH : PinState.LOW;

        GpioPinDigitalOutput pin = pinFactory.getDigitalPin(controlPin);

        if (pin == null) {
            return null;
        }

        // set shutdown state for this pin
        pin.setShutdownOptions(true, pinState);

        log.info("{} state was: {} ", pin.getName(), pin.getState());

        SensorValue sensorValue = new SensorValue();
        sensorValue.setPin(controlPin.getName());
        sensorValue.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // turn off/heatingOn gpio pin #17
        if (pinState.isLow()) {
            pin.low();
            sensorValue.setValue(0.0);
        } else if (pinState.isHigh()) {
            pin.high();
            sensorValue.setValue(1.0);
        }

        csvStatisticSaver.write(new SensorValue[]{sensorValue});
        sensorLastDataSaver.write(new SensorValue[]{sensorValue});

        return pin.getState();
    }

}
