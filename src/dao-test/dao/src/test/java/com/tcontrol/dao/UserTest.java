/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.dao;

import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author alexey
 */
public class UserTest {
    
    public UserTest() {
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
     * Test of getId method, of class User.
     */
    @Test
    public void testUserDefaultConstructor() {
        System.out.println("getId");
        User instance = new User();
        assertThat(instance.getId(), is(0));
        assertThat(instance.getRole(), is(0));
        assertNull(instance.getName());
        assertNull(instance.getSurname());
        
    }

       /**
     * Test of getId method, of class User.
     */
    @Test
    public void testUserConstructor1() {
        System.out.println("getId");
        User instance = new User(1,"alex","firster",666);
        assertThat(instance.getId(), is(1));
        assertThat(instance.getRole(), is(666));
        assertThat(instance.getName(), is("alex"));
        assertThat(instance.getSurname(),is("firster"));
        
    }
    /**
     * Test of setId method, of class User.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        int id = 0;
        User instance = new User();
        instance.setId(id);
        assertThat(instance.getId(),is(id));
    }

    /**
     * Test of setName method, of class User.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        User instance = new User();
        instance.setName(name);
        assertThat(instance.getName(),is(name));
    }

    /**
     * Test of setSurname method, of class User.
     */
    @Test
    public void testSetSurname() {
        System.out.println("setSurname");
        String surname = "";
        User instance = new User();
        instance.setSurname(surname);
        assertThat(instance.getSurname(), is(surname));
    }

    /**
     * Test of setRole method, of class User.
     */
    @Test
    public void testSetRole() {
        System.out.println("setRole");
        int role = 0;
        User instance = new User();
        instance.setRole(role);
        assertThat(instance.getRole(), is(role));
    }
    
}
