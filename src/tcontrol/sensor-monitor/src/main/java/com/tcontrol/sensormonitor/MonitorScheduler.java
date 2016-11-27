package com.tcontrol.sensormonitor;

import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import com.tcontrol.sensormonitor.properties.ApplicationProperty;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Collection;
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
    TemperatureMonitor temperatureMonitor;

    @EJB
    AlarmMonitor alarmMonitor;

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
    DaoInterface dao;

    @Timeout
    public void execute(Timer timer) throws DaoException {
        final Collection<Sensor> sensors = dao.getAllSensors().values();
        sensors.stream()
                .filter(sensor -> sensor.getType() == Sensor.SensorType.TEMPERATURE)
                .forEach(sensor -> {
                    try {
                        SensorValue value = temperatureMonitor.loadValue(sensor.getSerialNumber());
                        value.setSensorId(sensor.getId());
                        dao.addValues(Arrays.asList(value));
                        LOGGER.log(Level.INFO,
                                String.format(
                                        "Load temperature value, sensor id = %s , uuid = %s, t = %f ",
                                        sensor.getId(),
                                        sensor.getSerialNumber(),
                                        value.getValue()));
                    } catch (NoSuchFileException e) {
                        LOGGER.log(Level.INFO, String.format(
                                "Value is not available, sensor id = %s", sensor.getId()));
                    } catch (IOException e) {
                        LOGGER.log(Level.INFO,
                                String.format(
                                        "Failed to load temperature value, sensor id = %s",
                                        sensor.getId()), e);
                    }
                });

        sensors.stream()
                .filter(sensor -> sensor.getType() == Sensor.SensorType.ALARM)
                .forEach(sensor -> {
                    try {
                        SensorValue value = alarmMonitor.loadValue(sensor.getSerialNumber());
                        value.setSensorId(sensor.getId());
                        dao.addValues(Arrays.asList(value));
                        LOGGER.log(Level.INFO,
                                String.format(
                                        "Load alarm value, sensor id = %s , uuid = %s, t = %f ",
                                        sensor.getId(),
                                        sensor.getSerialNumber(),
                                        value.getValue()));
                    } catch (IOException e) {
                        LOGGER.log(Level.INFO,
                                String.format(
                                        "Failed to load alarm value, sensor id = %s",
                                        sensor.getId()), e);
                    }
                });
    }

}
