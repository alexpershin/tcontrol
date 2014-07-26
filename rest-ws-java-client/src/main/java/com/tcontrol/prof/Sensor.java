/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.prof;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alexey
 */
@XmlRootElement
public class Sensor {

    public Sensor() {
    }

    public Sensor(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    @XmlElement(name="name")
    public String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param nane new value of name
     */
    public void setName(String nane) {
        this.name = nane;
    }
    
    @XmlElement(name="id")
    public int id;

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String toString(){
        return "id = "+id+ ", name = "+name;
    }


}
