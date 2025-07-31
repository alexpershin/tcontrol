package org.tcontrol.handler.impl;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import lombok.RequiredArgsConstructor;
import org.tcontrol.config.props.OnOfHandlerConfig;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.handler.IOnOffHandler;
import org.tcontrol.handler.IRelayHandler;
import org.tcontrol.service.IThermostat;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class OnOffHandler implements IOnOffHandler {
    private final OnOfHandlerConfig onOfHandlerConfig;
    private final RelayPinsConfig relayPinsConfig;
    private final IRelayHandler relayHandler;
    private final IThermostat thermostat;

    @Override
    public boolean turnOnRelay() {

        return ofNullable(relayHandler.turnOnRelay(getPin()))
                .filter(Objects::nonNull)
                .map(PinState::isHigh).map(e->{
                    if(e){
                        thermostat.changeOn(false);
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
                        thermostat.changeOn(true);
                    }
                    return e;
                })
                .orElse(false);
    }

    private Pin getPin() {
        return relayPinsConfig.getPins()
                .stream()
                .filter(pin -> pin.getPinId() == onOfHandlerConfig.getPinId())
                .map(RelayPinsConfig.RelayPinConfig::getPin)
                .findFirst()
                .map(RaspiPin::getPinByName)
                .orElse(null);
    }

    @Override
    public int sensorId() {
        return onOfHandlerConfig.getSensorId();
    }
}
