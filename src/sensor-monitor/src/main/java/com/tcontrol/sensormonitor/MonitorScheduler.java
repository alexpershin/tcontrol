package com.tcontrol.sensormonitor;

import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import com.tcontrol.sensormonitor.properties.ApplicationProperty;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

/**
 *
 */
@Singleton
@Startup
public class MonitorScheduler {

    private static final Logger LOGGER = Logger.getLogger(MonitorScheduler.class.getName());

    @EJB
    private TemperatureMonitor temperatureMonitor;

    @Resource
    private TimerService timerService;

    @Inject
    @ApplicationProperty(name = "schedule.temperature")
    private ScheduleExpression scheduleExpression;
    
    @Inject
    @ApplicationProperty(name = "w1.devices.path")
    private String w1DevicesPath;

    @PostConstruct
    public void init() {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("TemperatureSensorShcheduletConfig");

        timerService.createCalendarTimer(scheduleExpression, timerConfig);
        
        temperatureMonitor.setW1DevicesPath(w1DevicesPath);
    }

    @EJB(beanName = "MySqlJDBCDaoImpl")
    private DaoInterface dao;

    @Timeout
    public void execute(Timer timer) throws DaoException {
        dao.getAllSensors().values().stream().filter(sensor -> {
            return sensor.getType() == Sensor.SensorType.TEMPERATURE;
        }).forEach(sensor -> {
            SensorValue value = temperatureMonitor.loadTemperatureValue(sensor.getSerialNumber());
            value.setSensorId(sensor.getId());
            dao.addValues(Arrays.asList(value));
            LOGGER.log(Level.INFO,
                    String.format(
                            "Load temperature value, sensor id = %s , uuid = %s ",
                            sensor.getId(),
                            sensor.getSerialNumber()));

        });
    }

}
