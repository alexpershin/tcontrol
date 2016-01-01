package com.tcontrol.webservice.sensor;

import com.tcontrol.dao.Sensor;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sensor to be passed to web UI. 
 */
@XmlRootElement
public class SensorWeb {

    @XmlElement(name = "id")
    private int id;

    @XmlElement(name = "type")//, type=String.class)
    private Sensor.SensorType type;

    @XmlElement(name = "name")
    private String name;
    
    @XmlElement(name = "description")
    private String description;

    /**
     * Constructs the sensors.
     */
    public SensorWeb() {
    }

    /**
     * Constructs the sensor.
     *
     * @param name sensor's name;
     * @param id sensor's id;
     */
    public SensorWeb(String name, int id, Sensor.SensorType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    /**
     * Gets the value of name.
     *
     * @return the value of name;
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of name.
     *
     * @param nane new value of name;
     */
    public void setName(String nane) {
        this.name = nane;
    }

    /**
     * Gets the value of id.
     *
     * @return the value of id;
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of id.
     *
     * @param id new value of id;
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the sensor type.
     *
     * @return the type;
     */
    public Sensor.SensorType getType() {
        return type;
    }

    /**
     * Sets the sensor type.
     *
     * @param type the type to set;
     */
    public void setType(Sensor.SensorType type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
