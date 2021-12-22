package com.tcontrol.prof.spring.web.service;

import jdk.nashorn.internal.objects.annotations.Property;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Configuration
@EnableScheduling
public class ScheduleConfig {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ScheduleConfig.class);

    @Bean
 //   @ConfigurationProperties(prefix = "scheduler.cron")
    public String cronExecExpr(){return new String();}// = "*/5 * * * * *";


    @Bean
    public String getConfigRefreshValue()
    {
        return "1000";
    }

    @Bean(name = "getCron")
    public String getCron()
    {
        return "*/5 * * * * *";
    }



    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
