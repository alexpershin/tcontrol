/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.sensormonitor;

import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author alexey
 */
@Singleton
@Startup
public class RealTimeMonitor {

    private static final Logger LOGGER = Logger.getLogger(RealTimeMonitor.class.getName());

    @EJB
    AlarmMonitor alarmMonitor;

    @EJB(beanName = "MySqlJDBCDaoImpl")
    DaoInterface dao;

    @PostConstruct
    public void init() {
        try {
            dao.getAllSensors().values().stream()
                    .filter(sensor -> sensor.getType() == Sensor.SensorType.ALARM)
                    .forEach(sensor -> {
                        alarmMonitor.addAlarmListener(sensor, v -> {
                            // display pin state on console
                            System.out.println(" --> GPIO PIN STATE CHANGE: " + v.getSensorId()
                                    + " = " + v.getValue());
                        });
                    });
        } catch (DaoException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
