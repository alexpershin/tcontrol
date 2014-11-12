package com.tcontrol.webservice.sensor;


import com.tcontrol.dao.Sensor;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alexey
 */
@XmlRootElement
public class SensorWeb {

    /**
     * Constructs the sensors.
     */
    public SensorWeb() {
    }

    /**
     * Constructs the sensor.
     * @param name sensor's name;
     * @param id sensor's id;
     */
    public SensorWeb(String name, int id, Sensor.SensorType type) {
        this.name = name;
        this.id = id;
        this.type=type;
    }
    
    @XmlElement(name="name")
    private String name;

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
    
    @XmlElement(name="id")
    private int id;

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
    
    @XmlElement(name="type")//, type=String.class)
    private Sensor.SensorType type;

    /**
     * Returns the sensor type.
     * @return the type;
     */
    public Sensor.SensorType getType() {
        return type;
    }

    /**
     * Sets the sensor type.
     * @param type the type to set;
     */
    public void setType(Sensor.SensorType type) {
        this.type = type;
    }
}
