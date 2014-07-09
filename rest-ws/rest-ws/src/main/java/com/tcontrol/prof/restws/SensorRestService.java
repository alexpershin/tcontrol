/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.prof.restws;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
@Path("generic")
public class SensorRestService {
    SensorsBean sensorsBean = lookupSensorsBeanBean();

    @Context
    private UriInfo context;
    
    

    /**
     * Creates a new instance of SensorRestService
     */
    public SensorRestService() {
    }

    /**
     * Retrieves representation of an instance of com.tcontrol.prof.restws.SensorRestService
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public List<Sensor> getXml() {
        return lookupSensorsBeanBean().sensors();       
    }

    /**
     * PUT method for updating or creating an instance of SensorRestService
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putXml(String content) {
    }

    private SensorsBean lookupSensorsBeanBean() {
        try {
            javax.naming.Context c = new InitialContext();
            return (SensorsBean) c.lookup("java:global/com.tcontrol.prof_rest-ws_war_1.0-SNAPSHOT/SensorsBean!com.tcontrol.prof.restws.SensorsBean");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
