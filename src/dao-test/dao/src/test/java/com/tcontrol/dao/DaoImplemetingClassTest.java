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
        
        List<Sensor> testList = new ArrayList<>;
        //TODO: add existing sensor ids into testList and feed this list to getSensors(List<Sensor> ids) method.
        //Then - get the list of Sensors being returned and compare to the pre-defined list
     
       Integer[] alreadyExistingSensorIds = {1, 2, 3, 4, 5};
               
        /*for (int i = 0; i < testList.size(); i++) {
            Sensor setId = testList    .get(i);
            
        } */
    
    }

}