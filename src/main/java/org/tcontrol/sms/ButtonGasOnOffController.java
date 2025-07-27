package org.tcontrol.sms;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tcontrol.service.IPinFactory;
import org.tcontrol.sms.config.props.ButtonConfig;

import javax.annotation.PostConstruct;


@Service
@Slf4j
@RequiredArgsConstructor
public class ButtonGasOnOffController {

    private final ButtonConfig buttonConfig;
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
                    gpio.provisionDigitalInputPin(buttonConfig.pin());

            pin.addListener(listener);
            log.info("Button listener added: " + pin.getPin().getName());
        } catch (Throwable e) {
            log.error(e);
        }
    }
}
