/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.prof.soapws.javaclient.console;

import javax.xml.ws.WebServiceRef;
import com.tcontrol.prof.soapws.javaclient.SensorsWebservice_Service;
import com.tcontrol.prof.soapws.javaclient.Sensor;
import com.tcontrol.prof.soapws.javaclient.SensorsWebservice;
import java.util.List;

/**
 *
 * @author alexey
 */
public class SensorsRequestConsoleTool {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            SensorsWebservice_Service service = new SensorsWebservice_Service();

            // Call Web Service Operation
            SensorsWebservice port = service.getSensorsWebservicePort();

            java.util.List<Sensor> sensors = port.sensors();

            logSensors(sensors);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static void logSensors(List<Sensor> sensors) {
        System.out.println("Sensors:");
        for (Sensor sensor : sensors) {
            System.out.println("id : " + sensor.getId());
            System.out.println("name : " + sensor.getName());
            System.out.println();
        }
    }

}
