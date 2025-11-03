package org.tcontrol.handler.impl;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.OnOfHandlerConfig;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.handler.IOnOffHandler;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.SensorServiceException;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Slf4j
@Logging
public class OnOffHandler implements IOnOffHandler {
    private final OnOfHandlerConfig onOfHandlerConfig;
    private final RelayPinsConfig relayPinsConfig;
    private final IRelayHandler relayHandler;
    private final IThermostat thermostat;

    @PostConstruct
    public void init(){
        log.info("init");
    }

    @Override
    public boolean turnOnRelay() {

        return ofNullable(relayHandler.turnOnRelay(getPin()))
                .filter(Objects::nonNull)
                .map(PinState::isHigh).map(e->{
                    if(e){
                        thermostat.off();
                    }
                    return e;
                })
                .orElse(false);
    }

    @Override
    public boolean turnOffRelay() {
        return ofNullable(relayHandler.turnOffRelay(getPin()))
                .filter(Objects::nonNull)
                .map(PinState::isHigh)
                .map(e->{
                    if(e){
                        thermostat.off();
                    }
                    return e;
                })
                .orElse(false);
    }

    private Pin getPin() {
        return relayPinsConfig.pinConfigurationById(onOfHandlerConfig.getPinId())
                .map(RelayPinsConfig.RelayPinConfiguration::getPin)
                .map(RaspiPin::getPinByName)
                .orElseThrow(()->new SensorServiceException(MessageFormat.format("Pin config not found by id: {0}", onOfHandlerConfig.getPinId())));
    }

    @Override
    public int sensorId() {
        return onOfHandlerConfig.getSensorId();
    }
}
