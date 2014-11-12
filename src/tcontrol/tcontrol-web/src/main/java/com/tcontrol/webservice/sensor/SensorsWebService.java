/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.webservice.sensor;

import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.Sensor.SensorType;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.NORMAL;
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.WARNING;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.Singleton;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author alexey
 */
@Path("/sensor")
@Singleton
public class SensorsWebService {
    /**
     * Creates a new instance of SensorsWebService
     */
    public SensorsWebService() {
    }

    /**
     * Current values.
     * @return list of values;
     */
    @GET
    @Path("/sensor_values")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorValueWeb> getSensorValues() {
        long timestamp=new Date().getTime();
        return Arrays.asList(
                new SensorValueWeb( 1, timestamp , 23.5, NORMAL),
                new SensorValueWeb(2,timestamp,-10.6, WARNING));
    }
    
    /**
     * Returns sensors.
     * @return sensors list;
     */
    @GET
    @Path("/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorWeb> getSensors() {
        return Arrays.asList(
                new SensorWeb("indor", 1, SensorType.TEMPERATURE),
                new SensorWeb("outdor", 2, SensorType.TEMPERATURE));
    }
    
}
