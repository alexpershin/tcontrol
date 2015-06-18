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
public class SensorValuesResult {
    
    
    @XmlElement(name = "values")
    private SensorValueWeb[] values;

    /**
     * @return the sensors
     */
    public SensorValueWeb[] getValues() {
        return values;
    }

    /**
     * @param sensors the sensors to set
     */
    public void setValues(SensorValueWeb[] values) {
        this.values = values;
    }
    
}
