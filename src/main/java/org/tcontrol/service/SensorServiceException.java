package org.tcontrol.service;

/**
 * Base DAO exception.
 */
public class SensorServiceException extends Exception{
   public SensorServiceException(Throwable cause) {
        super("Sensor service interface exception", cause);
    } 
}
