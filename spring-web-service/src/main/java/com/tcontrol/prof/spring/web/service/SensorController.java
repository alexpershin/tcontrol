package com.tcontrol.prof.spring.web.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Web Service.
 */
@RestController
public class SensorController {
    /**
     * Creates a new instance of GenericResource.
     */
    public SensorController() {
    }
    
    @RequestMapping("/sensors")
    public List<Sensor> getSensors() {
        return Arrays.asList(new Sensor("indor",1), new Sensor("outdor",2));
    }
}
