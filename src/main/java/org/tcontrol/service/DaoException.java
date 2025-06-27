package org.tcontrol.service;

/**
 * Base DAO exception.
 */
public class DaoException extends Exception{
   public DaoException(Throwable cause) {
        super("DAO interface exception", cause);
    } 
}
