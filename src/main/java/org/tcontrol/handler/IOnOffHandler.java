package org.tcontrol.handler;

public interface IOnOffHandler {
    boolean turnOnRelay();

    boolean turnOffRelay();

    int sensorId();
}
