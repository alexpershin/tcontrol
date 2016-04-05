package com.tcontrol.webservice.sensor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sensor value to be passed to web UI.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SensorValueWeb {

    public static enum SensorValueState {
        NORMAL,
        WARNING,
        ALERT
    }
    @XmlElement(name = "sensorId")
    private int sensorId;
    
    @XmlElement(name = "timestamp")
    private long timestamp;
    
    @XmlElement(name = "value")
    private double value;
    
    /**
     * Delta for the last hour.
     */
    @XmlElement(name = "gradient")
    private Double gradient;
    
    @XmlElement(name = "state")
    private SensorValueState state;

    /**
     * Default constructor
     */
    public SensorValueWeb() {
    }

    
    
    /**
     * Returns current value of the sensor.
     * @param sensorId sensor's id;
     * @param timestamp time stamp when value was got;
     * @param value the value;
     * @param state the value state;
     * @param gradient the gradient;
     */
    public SensorValueWeb(int sensorId, long timestamp, 
            double value, SensorValueState state, Double gradient) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.value = value;
        this.state = state;
        this.gradient = gradient;
    }

    /**
     * @return the sensorId
     */
    public int getSensorId() {
        return sensorId;
    }

    /**
     * @param sensorId the sensorId to set
     */
    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the state
     */
    public SensorValueState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(final SensorValueState state) {
        this.state = state;
    }
    
    /**
     * @return the gradient
     */
    public Double getGradient() {
        return gradient;
    }

    /**
     * @param gradient the gradient to set
     */
    public void setGradient(Double gradient) {
        this.gradient = gradient;
    }

}
