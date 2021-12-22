package com.tcontrol.prof.spring.web.service;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledFuture;

@Component
public  class Scheduler implements Runnable {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Scheduler.class);
    @SuppressWarnings("rawtypes")
    ScheduledFuture scheduledFuture;

    TaskScheduler taskScheduler;

    String cronExp = "*/5 * * * * *";

    //this method will kill previous scheduler if exists and will create a new scheduler with given cron expression
    public  void reSchedule(String cronExpressionStr){
        if(taskScheduler== null){
            this.taskScheduler = new ConcurrentTaskScheduler();
        }
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(true);
        }
        this.scheduledFuture = this.taskScheduler.schedule(this, new CronTrigger(cronExpressionStr));
    }

    @Override
    public  void run(){
        LOG.debug("schedule someTask");
    }

    //if you want on application to read data on startup from db and schedule the schduler use following method
    @PostConstruct
    public void initializeScheduler() {
        //@postcontruct method will be called after creating all beans in application context
        // read user config map from db
        // get cron expression created
        this.reSchedule(cronExp);
    }

}
