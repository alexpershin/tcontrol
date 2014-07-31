/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.prof.restws.client;

import com.tcontrol.prof.Sensor;

/**
 *
 * @author alexey
 */
public class ClientConsoleTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JerseySensorsClient client = new JerseySensorsClient();
        try {
            String result = client.getStatus();
            System.out.println("Status=" + result);
            System.out.print("Sensors:");
            for (Sensor sensor : client.getSensors()) {
                System.out.print(sensor + ";");
            }
            System.out.println();

        } finally {
            client.close();
        }
    }

}
