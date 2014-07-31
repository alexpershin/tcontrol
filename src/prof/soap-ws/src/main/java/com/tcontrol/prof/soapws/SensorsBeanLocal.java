/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.prof.soapws;

import javax.ejb.Local;

/**
 *
 * @author alexey
 */
@Local
public interface SensorsBeanLocal {

    java.util.List<Sensor> sensors();
}
