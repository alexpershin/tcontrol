/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.prof.restws.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.tcontrol.prof.Sensor;
import java.util.List;

/**
 * @author alexey
 */
public class JerseySensorsClient {

    private WebResource webTarget;
    private Client client;
    private static final String BASE_URI
            = "http://localhost:8080/rest-ws-web/webresources";

    public JerseySensorsClient() {
        ClientConfig clientConfig = new DefaultClientConfig();

        clientConfig.getFeatures().put(
                JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        client = Client.create(clientConfig);
        webTarget = client.resource(BASE_URI);
    }

    public List<Sensor> getSensors() {
        WebResource resource = webTarget;
        List<Sensor> sensors = resource.path("sensors").get(new GenericType<List<Sensor>>() {
        });
        return sensors;
    }

    public String getStatus() {
        WebResource resource = webTarget;
        return resource.path("status").get(String.class);
    }

    public void close() {
        client.destroy();
    }

}
