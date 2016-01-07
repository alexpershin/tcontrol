package com.tcontrol.prof.sensormonitor;

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

    @PostConstruct
    public void init() {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("TemperatureSensorShcheduletConfig");
        ScheduleExpression scheduleExp = new ScheduleExpression();
        scheduleExp.hour("*").minute("*").second("*/30");
        timerService.createCalendarTimer(scheduleExp, timerConfig);
    }

    @Timeout
    public void execute(Timer timer) {
        temperatureMonitor.requestValue();
        LOGGER.log(Level.INFO, "Load temperature value, sensor id=1, uuid=#reyyr476");
    }

}
