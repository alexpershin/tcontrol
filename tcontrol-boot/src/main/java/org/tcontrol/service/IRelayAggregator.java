package org.tcontrol.service;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import org.tcontrol.handler.IRelayHandler;

public interface IRelayAggregator extends IRelayHandler {
    PinState turnOnRelay(Pin controlPin);

    PinState turnOffRelay(Pin controlPin);

    PinState getPinState(Pin controlPin);
}
