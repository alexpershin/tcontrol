package org.tcontrol.service;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

public interface IPinFactory {
    GpioPinDigitalOutput getDigitalPin(Pin controlPin);
}
