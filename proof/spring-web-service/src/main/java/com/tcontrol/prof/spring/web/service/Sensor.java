package com.tcontrol.prof.spring.web.service;

public class Sensor {

    public Sensor() {
    }

    public Sensor(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    public String name;

    /**
     * Get the value of name.
     *
     * @return the value of name;
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name.
     *
     * @param nane new value of name;
     */
    public void setName(String nane) {
        this.name = nane;
    }
    
    public int id;

    /**
     * Get the value of id.
     *
     * @return the value of id;
     */
    public int getId() {
        return id;
    }

    /**
     * Set the value of id.
     *
     * @param id new value of id;
     */
    public void setId(int id) {
        this.id = id;
    }


}
