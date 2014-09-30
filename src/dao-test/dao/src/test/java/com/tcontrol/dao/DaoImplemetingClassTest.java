/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao;

/**
 *
 * @author Anton Buslavskii
 */


import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;


public class DaoImplemetingClassTest {

    public DaoImplemetingClassTest() {

    }
     
    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    
    /**
     * Test of 999 method, of class DaoImplemetingClass.
     */
    @Test
    public void testGettingSensors() {
        System.out.println("check of existing sensors");
        DaoImplemetingClass daoImplemInstance = new DaoImplemetingClass();
        
        List<Sensor> testList = new ArrayList<Sensor>();
        //TODO: add existing sensor ids into testList and feed this list to getSensors(List<Sensor> ids) method.
        //Then - get the list of Sensors being returned and compare to the pre-defined list
     
       Integer[] alreadyExistingSensorIds = {1, 2, 3, 4, 5}; //we have sensors with such IDs
       Double[] alreadyExistingSensorValues = {1.0, 2.0, 3.0, 4.0, 5.8}; //TODO: add existing values, not fake data
               
        for (int i = 0; i < alreadyExistingSensorIds.length - 1 ; i++) {
            Sensor sensorWithId = new Sensor();
            sensorWithId.setId(alreadyExistingSensorIds[i]);
            testList.add(sensorWithId);
         } //here we have our List of Sensor objects with set IDs
        
        System.out.println("start of a test");
        DaoImplemetingClass daoImplClassInstance = new DaoImplemetingClass();
      //  daoImplClassInstance.getSensors(testList);
        
       
    
    }

}
