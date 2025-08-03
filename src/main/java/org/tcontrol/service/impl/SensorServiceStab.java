package org.tcontrol.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.tcontrol.config.props.RelayPinsConfig;
import org.tcontrol.mappers.SensorMapper;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.IThermostat;
import org.tcontrol.service.SensorServiceException;
import org.tcontrol.service.models.Sensor;
import org.tcontrol.service.models.SensorValue;
import org.springframework.stereotype.Component;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.config.props.SensorConfiguration;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stub
 */
@Component
@ConditionalOnProperty(value = "main-config.sensorsStub", havingValue = "true")
public class SensorServiceStab extends SensorServiceImpl {

    @Autowired
    private ApplicationContext appContext;

    public SensorServiceStab(SensorConfig sensorConfig, RelayPinsConfig pinsConfig,
                             IStatisticsSaver sensorLastDataSaver, SensorMapper sensorMapper,
                             List<IThermostat> thermostats) {
        super(sensorConfig, pinsConfig, sensorLastDataSaver, sensorMapper, thermostats);
    }



//    @Override
//    public int startHeating(int sensorId, float temperature) {
//        SensorValue sv = sensorLastDataSaver.lastValues(sensorId).getLast();
//        SensorConfiguration sc = getSc(sensorId);
//        if (sv != null) {
//            sv.setState(SensorValue.SensorState.ON);
//            sv.setValue(1);
//            sensorLastDataSaver.write(Collections.singletonList(sv));
//            return 1;
//        }
//        return -1;
//    }
//
//    @Override
//    public int stopHeating(int sensorId) {
//        SensorValue sv = sensorLastDataSaver.lastValues(sensorId).getLast();
//        SensorConfiguration sc = getSc(sensorId);
//
//        if (sv != null ) {
//            sv.setState(SensorValue.SensorState.OFF);
//            sv.setValue(0);
//            sensorLastDataSaver.write(Collections.singletonList(sv));
//            return 0;
//        }
//        return -1;
//    }
//
//    private SensorConfiguration getSc(int sensorId) {
//        return sensorConfig.getSensors().stream()
//                .filter(s -> s.getId() == sensorId & s.getType() == Sensor.SensorType.ON_OFF)
//                .findFirst()
//                .orElseThrow(() -> new SensorServiceException(MessageFormat.format("Sensor config not found by sensorId: {0} and type {1}", sensorId, Sensor.SensorType.ON_OFF)));
//    }
}
