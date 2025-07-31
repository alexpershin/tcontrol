package org.tcontrol.sms.handler.impl;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tcontrol.service.IPinFactory;
import org.tcontrol.sms.IThermostat;
import org.tcontrol.sms.config.props.ButtonHandlerConfig;

import javax.annotation.PostConstruct;


@Service
@Slf4j
@RequiredArgsConstructor
public class ButtonGasOnOffHandler {

    private final ButtonHandlerConfig buttonHandlerConfig;
    private final IThermostat thermostatGas;
    private final IPinFactory pinFactory;

    private GpioController gpio;

    @PostConstruct
    public void init() {
        try {
            gpio = GpioFactory.getInstance();

            GpioPinListenerDigital listener = event -> {
                PinState value = event.getState();

                // display output
                log.info("BUTTON state: " + value.isHigh());
                if(value.isLow()){
                    boolean thetmostatNewState = !thermostatGas.isOn();
                    log.info("Thermostat new state: " + thetmostatNewState);
                    thermostatGas.changeOn(thetmostatNewState);
                }
            };

            final GpioPinDigitalInput pin =
                    gpio.provisionDigitalInputPin(buttonHandlerConfig.pin());

            pin.addListener(listener);
            log.info("Button listener added: " + pin.getPin().getName());
        } catch (Throwable e) {
            log.error("", e);
        }
    }
}
