/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.prof.soapws;

import java.util.Arrays;
import javax.ejb.Stateless;

/**
 *
 * @author alexey
 */
@Stateless
public class SensorsBean implements SensorsBeanLocal {

    @Override
    public java.util.List<Sensor> sensors() {
        return Arrays.asList(new Sensor(1,"indor"), new Sensor(2,"outdor"));
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
