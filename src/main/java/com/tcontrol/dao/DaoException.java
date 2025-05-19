package com.tcontrol.dao;

/**
 * Base DAO exception.
 */
public class DaoException extends Exception{
   public DaoException(Throwable cause) {
        super("DAO interface exception", cause);
    } 
}
