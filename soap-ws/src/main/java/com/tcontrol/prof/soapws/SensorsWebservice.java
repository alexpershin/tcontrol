/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.prof.soapws;

import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.ejb.Stateless;
import javax.jws.WebMethod;

/**
 *
 * @author alexey
 */
@WebService(serviceName = "SensorsWebservice")
@Stateless()
public class SensorsWebservice {

    @EJB
    private SensorsBeanLocal ejbRef;

    @WebMethod(operationName = "sensors")
    public List<Sensor> sensors() {
        return ejbRef.sensors();
    }
}
