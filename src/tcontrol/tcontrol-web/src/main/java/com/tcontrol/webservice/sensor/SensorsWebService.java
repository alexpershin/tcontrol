package com.tcontrol.webservice.sensor;

import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.ALERT;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.NORMAL;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.WARNING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST Web Service.
 */
@Path("/sensor")
@Singleton
public class SensorsWebService {

    //@EJB(beanName = "MySqlJDBCDaoImpl")
    @EJB(beanName = "DaoStub")
    private DaoInterface dao;

    private int currentUserId = 1;

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
    public SensorValuesResult getSensorValues() throws DaoException {
        SensorValuesResult result = new SensorValuesResult();
        List<SensorValue> values = dao.getCurrentValues(currentUserId);
        List<SensorValueWeb> webValues = converSensorValuesToWeb(values);
        result.setValues(webValues.toArray(new SensorValueWeb[]{}));
        return result;
    }

    List<SensorValueWeb> converSensorValuesToWeb(List<SensorValue> values) throws DaoException {

        List<SensorValueWeb> webValues = new ArrayList<>();

        final Map<Integer, Sensor> allSensors = dao.getAllSensors();

        for (SensorValue value : values) {

            Sensor sensor = allSensors.get(value.getSensorId());

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
        SensorsResult result = new SensorsResult();
        Collection<Sensor> sensors;
        try {
            sensors = dao.getUserSensors(currentUserId).values();
        } catch (DaoException ex) {
            throw buildWebApplicationException(ex);
        }
        List<SensorWeb> webSensors = convertSensorToWeb(sensors);
        result.setSensors(webSensors.toArray(new SensorWeb[]{}));
        return result;
    }

    private WebApplicationException buildWebApplicationException(DaoException ex) {
        Logger.getLogger(SensorsWebService.class.getName()).log(Level.SEVERE, null, ex);
        throw new WebApplicationException(
                Response.status(Status.INTERNAL_SERVER_ERROR).entity(
                //JSON object can be returned here. The object must be XmElement
                // new ExceptionInfo(Status.NOT_FOUND.getStatusCode(), "DAO erorr", "DAO error description")
                ex.getMessage() + ":\n" + ex.getCause().getMessage()
        ).type("application/json").build());
    }

    List<SensorWeb> convertSensorToWeb(Collection<Sensor> sensors) {
        List<SensorWeb> webSensors = new ArrayList<>();
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

        SensorValueWeb.SensorValueState result = NORMAL;

        double v = value.getValue();
        if (Sensor.SensorType.ALARM == sensor.getType()
                || Sensor.SensorType.ON_OFF == sensor.getType()) {
            result = value.getValue() == 0 ? NORMAL : ALERT;

        } else //calculate state according to threshold
         //low thressHold
            if (value.getValue() <= sensor.getLowThreshold()) {
                result = ALERT;
            } else if (v <= sensor.getLowThreshold() + sensor.getThresholdLag()
                    && v > sensor.getLowThreshold()) {
                result = WARNING;
            } //high thressHold
            else if (v >= sensor.getHighThreshold()) {
                result = ALERT;
            } else if (v >= sensor.getHighThreshold() - sensor.getThresholdLag()
                    && v < sensor.getHighThreshold()) {
                result = WARNING;
            }
        return result;
    }

}
