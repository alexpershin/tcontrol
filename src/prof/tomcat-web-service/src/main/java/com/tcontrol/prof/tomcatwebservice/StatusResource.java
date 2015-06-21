/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.prof.tomcatwebservice;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author alexey
 */
@Path("status")
@RequestScoped
public class StatusResource {

    @Context
    private UriInfo context;

    public StatusResource() {
    }

    @GET
    @Produces(APPLICATION_JSON)
    public String getXml() {
        return "ok";
    }
    
}
