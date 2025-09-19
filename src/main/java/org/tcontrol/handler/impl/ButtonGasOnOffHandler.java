package org.tcontrol.handler.impl;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.service.IThermostat;
import org.tcontrol.config.props.ButtonHandlerConfig;

import javax.annotation.PostConstruct;

import static org.tcontrol.service.impl.Thermostat.TEMPERATURE_MAX_ON;

@Slf4j
@RequiredArgsConstructor
public class ButtonGasOnOffHandler {

    private final ButtonHandlerConfig buttonHandlerConfig;
    private final IThermostat thermostatGas;

    @PostConstruct
    public void init() {
        try {
            GpioController gpio = GpioFactory.getInstance();

            GpioPinListenerDigital listener = event -> {
                PinState value = event.getState();

                // display output
                log.info("BUTTON state: {}", value.isHigh());
                if(value.isLow()){
                    boolean thermostatNewState = !thermostatGas.isOn();
                    log.info("Thermostat new state: {}", thermostatNewState);
                    if(thermostatNewState)
                        thermostatGas.on(TEMPERATURE_MAX_ON);
                    else
                        thermostatGas.off();
                }
            };

            final GpioPinDigitalInput pin =
                    gpio.provisionDigitalInputPin(buttonHandlerConfig.pin());

            pin.addListener(listener);
            log.info("Button listener added: {}", pin.getPin().getName());
        } catch (Throwable e) {
            log.error("", e);
        }
    }
}
