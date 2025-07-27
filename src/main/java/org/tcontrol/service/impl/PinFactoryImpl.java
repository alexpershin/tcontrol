package org.tcontrol.service.impl;

import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tcontrol.service.IPinFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PinFactoryImpl implements IPinFactory {

    private GpioController gpio;
    final private Map<String, GpioPinDigitalOutput> pins = new HashMap<>();

    @PostConstruct
    private void init(){

        log.info("Initializing pin factory ...");
        // create gpio controller
        try {
            gpio = GpioFactory.getInstance();
            log.info("Initializing pin factory COMPLETE");
        } catch (Throwable e) {
            log.error("", e);
        }
    }

    @Override
    public GpioPinDigitalOutput getDigitalPin(Pin controlPin) {
        if (gpio == null) {
            log.info("Initializing output pin {} {}: {}", controlPin.getName(), "FAILED", "GPIO not initiated");
            return null;
        }
        GpioPinDigitalOutput gpioPinDigitalOutput = pins.get(controlPin.getName());
        if (gpioPinDigitalOutput == null) {
            log.info("Initializing output pin: " + controlPin.getName());
            gpioPinDigitalOutput = gpio
                    .provisionDigitalOutputPin(controlPin, "heat_relay_" + controlPin.getAddress(),
                            PinState.LOW);
            pins.put(controlPin.getName(), gpioPinDigitalOutput);
            log.info("Initializing output pin (" + controlPin.getName() + ") COMPLETE");
        }
        log.info("Output pin provided: " + gpioPinDigitalOutput.getName());

        return gpioPinDigitalOutput;
    }
}
