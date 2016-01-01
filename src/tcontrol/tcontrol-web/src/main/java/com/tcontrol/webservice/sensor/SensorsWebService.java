package com.tcontrol.webservice.sensor;

import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service.
 */
@Path("/sensor")
@Singleton
public class SensorsWebService {

    @Inject
    DaoInterface dao;

    /**
     * Creates a new instance of SensorsWebService
     */
    public SensorsWebService() {
    }

    /**
     * Current values.
     *
     * @return list of values;
     */
    @GET
    @Path("/sensor_values")
    @Produces(MediaType.APPLICATION_JSON)
    public SensorValuesResult getSensorValues() {
        SensorValuesResult result=new SensorValuesResult();
        List<SensorValue> values = dao.getCurrentValues(0);
        List<SensorValueWeb> webValues = converSensorValuesToWeb(values);
        result.setValues(webValues.toArray(new SensorValueWeb[]{}));
        return result;
    }

    List<SensorValueWeb> converSensorValuesToWeb(List<SensorValue> values) {
        List<SensorValueWeb> webValues = new ArrayList<>();
        for (SensorValue value : values) {
            Sensor sensor = dao.getAllSensors().get(value.getSensorId());

            SensorValueWeb webValue = new SensorValueWeb(
                    value.getSensorId(),
                    value.getTimestamp().getTime(),
                    value.getValue(),
                    calculateSate(value, sensor)
            );
            webValues.add(webValue);
        }
        return webValues;
    }

    /**
     * Returns sensors.
     *
     * @return sensors list;
     */
    @GET
    @Path("/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    public SensorsResult getSensors() {
        SensorsResult result=new SensorsResult();
        Collection<Sensor> sensors = dao.getAllSensors().values();
        List<SensorWeb> webSensors = convertSensorToWeb(sensors);
        result.setSensors(webSensors.toArray(new SensorWeb[]{}));
        return result;
    }

    List<SensorWeb> convertSensorToWeb(Collection<Sensor> sensors) {
        List<SensorWeb> webSensors = new ArrayList<SensorWeb>();
        for (Sensor sensor : sensors) {
            final SensorWeb sensorWeb = new SensorWeb(
                    sensor.getName(),
                    sensor.getId(),
                    sensor.getType()
            );
            sensorWeb.setDescription(sensor.getDescription());

            webSensors.add(sensorWeb);
        }
        return webSensors;
    }

    static SensorValueWeb.SensorValueState calculateSate(SensorValue value,
            Sensor sensor) {

        SensorValueWeb.SensorValueState result
                = SensorValueWeb.SensorValueState.NORMAL;

        double v = value.getValue();

        //low thressHold
        if (value.getValue() <= sensor.getLowThreshold()) {
            result = SensorValueWeb.SensorValueState.ALERT;
        } else if (v <= sensor.getLowThreshold() + sensor.getThresholdLag()
                && v > sensor.getLowThreshold()) {
            result = SensorValueWeb.SensorValueState.WARNING;
        } //high thressHold
        else if (v >= sensor.getHighThreshold()) {
            result = SensorValueWeb.SensorValueState.ALERT;
        } else if (v >= sensor.getHighThreshold() - sensor.getThresholdLag()
                && v < sensor.getHighThreshold()) {
            result = SensorValueWeb.SensorValueState.WARNING;
        }
        return result;
    }

}
