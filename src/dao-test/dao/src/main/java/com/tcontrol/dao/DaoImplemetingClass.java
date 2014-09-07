/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anton Buslavskii
 */
public class DaoImplemetingClass implements DaoInterface {

    private static final String dbDriver = "here_is_the_driver";
    private static final String dbConn = "connection_data";
    private static final String dbUser = "user";
    private static final String dbPassword = "password";

    private static Connection getDbConnection() {

        Connection dbConnection = null;

        try {

            Class.forName(dbDriver);

        } catch (ClassNotFoundException e) {

            System.out.println(e.getMessage());

        }

        try {

            dbConnection = DriverManager.getConnection(
                    dbConn, dbUser, dbPassword);
            return dbConnection;

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

        return dbConnection;

    }

    @Override
    public List<Sensor> getSensors() {

        Connection dbConnection = null;
        PreparedStatement preparedStatement;
        List<Sensor> listOfSensorsToReturn = new ArrayList<>();
        Sensor sensorVariable;
        
        try {
            String selectSQL = "SELECT id from dbtcontrol.sensors where id = ?";
           
            /*PreparedStatement */ preparedStatement = dbConnection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, 1001); 
            ResultSet rs = preparedStatement.executeQuery(selectSQL);
            
            while (rs.next()) {
                              
                System.out.println( "RS is " + rs);
                String userid = rs.getString("id");
                Sensor snsr = new Sensor();
                snsr.setId(Integer.parseInt(userid));
                listOfSensorsToReturn.add(snsr);
               
            }

            throw new SQLException("Impossible to get sensors from the DB."); //To change body of generated methods, choose Tools | Templates.
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplemetingClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        //TODO: implement  listOfSensorsToReturn!!!
        return listOfSensorsToReturn;
    }

    @Override
    public List<Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addValues(int sensorId, ArrayList<SensorValue> values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer addSensor(String sensorType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveSensor(Sensor sensor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addSensorToUser(int sensorId, int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSensorFromUser(int userId, int sensorId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getSensorIdsLinkedToUser(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateUser(User user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeUser(int userId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
