/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.prof.tomcatwebservice;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author alexey
 */
@Path("sensors")
@RequestScoped
public class SensorsResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public SensorsResource() {
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors() {
        return Arrays.asList(new Sensor("indor",1), new Sensor("outdor",2));
    }
}
