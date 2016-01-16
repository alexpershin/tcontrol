package com.tcontrol.webservice.sensor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sensors wrapper to be passed to web UI.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
