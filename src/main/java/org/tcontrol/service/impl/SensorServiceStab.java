package org.tcontrol.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.IThermostat;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.SensorConfig;

import java.util.*;

/**
 * Stub
 */
@Component
@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
public class SensorServiceStab extends SensorServiceImpl {


    public SensorServiceStab(SensorConfig sensorConfig, RelayPinsConfig pinsConfig,
                             IStatisticsSaver sensorLastDataSaver, SensorMapper sensorMapper,
                             List<IThermostat> thermostats) {
        super(sensorConfig, pinsConfig, sensorLastDataSaver, sensorMapper, thermostats);
    }

}
