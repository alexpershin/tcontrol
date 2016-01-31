package com.tcontrol.prof.sensormonitor;

import com.tcontrol.prof.properties.ApplicationProperty;
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

    @PostConstruct
    public void init() {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("TemperatureSensorShcheduletConfig");

        timerService.createCalendarTimer(scheduleExpression, timerConfig);
    }

    
    @Timeout
    public void execute(Timer timer) {
        temperatureMonitor.requestValue();
        LOGGER.log(Level.INFO, "Load temperature value, sensor id=1, uuid=#reyyr476");
    }

}
