package org.tcontrol.handler;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public interface IRelayHandler {
    PinState turnOnRelay(Pin controlPin);

    PinState turnOffRelay(Pin controlPin);

    PinState getPinState(Pin controlPin);
}
