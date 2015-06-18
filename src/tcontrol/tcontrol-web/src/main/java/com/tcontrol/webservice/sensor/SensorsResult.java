/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.webservice.sensor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alexey
 */
@XmlRootElement
public class SensorsResult {
    
    
    @XmlElement(name = "sensors")
    private SensorWeb[] sensors;

    /**
     * @return the sensors
     */
    public SensorWeb[] getSensors() {
        return sensors;
    }

    /**
     * @param sensors the sensors to set
     */
    public void setSensors(SensorWeb[] sensors) {
        this.sensors = sensors;
    }
    
}
