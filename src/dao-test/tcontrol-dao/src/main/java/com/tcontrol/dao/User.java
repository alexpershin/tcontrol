package com.tcontrol.dao;

/**
 *
 * @author Anton Buslavskii
 */
public class User {

    private int id;
    private String name;
    private String surname;
    private int role;

    public User() {
    }

    public User(int id, String name, String surname, int role) {
        this();
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
