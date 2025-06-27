package org.service;

import org.tcontrol.service.models.User;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
public class UserTest {

    public UserTest() {
    }

    /**
     * Test of getId method, of class User.
     */
    @Test
    public void testUserDefaultConstructor() {
        System.out.println("getId");
        User instance = new User();
        assertEquals(instance.getId(), 0);
        assertEquals(instance.getRole(), 0);
        assertNull(instance.getName());
        assertNull(instance.getSurname());

    }

    /**
     * Test of getId method, of class User.
     */
    @Test
    public void testUserConstructor1() {
        System.out.println("getId");
        User instance = new User(1, "alex", "firster", 666);
        assertEquals(instance.getId(), 1);
        assertEquals(instance.getRole(), 666);
        assertEquals(instance.getName(), "alex");
        assertEquals(instance.getSurname(), "firster");

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
        assertEquals(instance.getId(), id);
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
        assertEquals(instance.getName(), name);
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
        assertEquals(instance.getSurname(), surname);
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
        assertEquals(instance.getRole(), role);
    }

}
