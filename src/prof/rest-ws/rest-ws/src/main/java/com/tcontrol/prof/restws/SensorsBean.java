/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.prof.restws;

import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author alexey
 */
@Stateless
public class SensorsBean {

    public List<Sensor> sensors() {
        return Arrays.asList(new Sensor(1,"indor"), new Sensor(2,"outdor"));    
    }
    
}
