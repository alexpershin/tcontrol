package com.tcontrol.webservice.sensor;

import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *  Application configuration class.
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = super.getProperties();
        //properties.put(Properties.UNWRAP_ROOT_VALUE, false);
        
        return properties;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.tcontrol.webservice.sensor.SensorsWebService.class);
    }
    
}
