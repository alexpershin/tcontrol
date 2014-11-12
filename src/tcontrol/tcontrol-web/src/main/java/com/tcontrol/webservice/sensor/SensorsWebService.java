/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.webservice.sensor;

import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author alexey
 */
@Path("sensors")
@RequestScoped
public class SensorsWebService {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SensorsWebService
     */
    public SensorsWebService() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorWeb> getSensors() {
        return Arrays.asList(new SensorWeb("indor",1), new SensorWeb("outdor",2));
    }
}
