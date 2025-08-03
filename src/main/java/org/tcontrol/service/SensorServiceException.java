package org.tcontrol.service;

/**
 * Base DAO exception.
 */
public class SensorServiceException extends RuntimeException{
    public SensorServiceException(Throwable cause) {
        super("Sensor service interface exception", cause);
    }
    public SensorServiceException(String message) {
        super(message);
    }
}
