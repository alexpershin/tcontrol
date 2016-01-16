package com.tcontrol.webservice.sensor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sensor values wrapper to be passed to web UI.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
     * @param values the sensors to set;
     */
    public void setValues(SensorValueWeb[] values) {
        this.values = values;
    }
    
}
