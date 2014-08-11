/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcontrol.dao.dao;

/**
 *
 * @author Anton Buslavskii
 */
public interface User {
    
    /** Adds a new user with default role
     *
     * @param userLogin
     * @param userPassword
     * @return The userId (integer) of recently created user
     */
    int addUser (String userLogin, String userPassword);
    
    
    
    void editUser (int userId, String userPassword);
    
    /** Removes the specified user with given ID
     *
     * @param userId
     */
    void removeUser (int userId);
}