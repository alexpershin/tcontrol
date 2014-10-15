
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
 * TODO remove this class, all logic should be moved to MySqlJDBCDaoImpl
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
    public List<Sensor> getAllSensors() {

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


                System.out.println("RS is " + rs);
                String userid = rs.getString("id");
                Sensor snsr = new Sensor();
                snsr.setId(Integer.parseInt(userid));
                listOfSensorsToReturn.add(snsr);


            }

            throw new SQLException("Impossible to get sensors from the DB."); //To change body of generated methods, choose Tools | Templates.
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplemetingClass.class.getName()).log(Level.SEVERE, null, ex);
        }


        return listOfSensorsToReturn;
    }

    @Override
    public List<Sensor> getSensors(List<Integer> ids) {

        int sizeOfListInInputtedParameter = ids.size();
        //System.out.println("Size of list being inputted is " + sizeOfListInInputtedParameter);
        List<Sensor> listOfSensorsWithExactIdsToReturn = new ArrayList<>();

        Connection dbConnection = null;
        PreparedStatement preparedStatement;
        Sensor sensorVariable;

        int[] sensorsIds = new int[sizeOfListInInputtedParameter];

        try {

            for (int i = 1; i < sensorsIds.length; i++) {

                String selectSQL = "SELECT id from dbtcontrol.sensors where id = ?";

                /*PreparedStatement */ preparedStatement = dbConnection.prepareStatement(selectSQL);
                preparedStatement.setInt(1, 1001);
                ResultSet rs = preparedStatement.executeQuery(selectSQL);

                while (rs.next()) {

                    System.out.println("RS is " + rs);
                    String userid = rs.getString("id");
                    Sensor snsr = new Sensor();
                    snsr.setId(Integer.parseInt(userid));
                    listOfSensorsWithExactIdsToReturn.add(snsr);

                }

                throw new SQLException("Impossible to get sensors from the DB."); //To change body of generated methods, choose Tools | Templates.
            }
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplemetingClass.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listOfSensorsWithExactIdsToReturn;

    };
    
    
    @Override
    public List<SensorValue> getCurrentValues(int userId) {


        Connection dbConnection = null;
        PreparedStatement preparedStatement;
        List<SensorValue> listOfCurrentValuesForExactUserToReturn = new ArrayList<>();
        SensorValue sensorVariable = new SensorValue();




        try {
            String selectSQL = "SELECT value from dbtcontrol.sensor_values where sensor_id  in (select sensor_id from dbtcontrol.users where id = ?))";


            /*PreparedStatement */ preparedStatement = dbConnection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery(selectSQL);


            while (rs.next()) {


                //System.out.println( "RS is " + rs);
                String sensorStringValue = rs.getString("value");
                //SensorValue snsrValue = new SensorValue();


                sensorVariable.setValue(Integer.parseInt(sensorStringValue));
                listOfCurrentValuesForExactUserToReturn.add(sensorVariable);


            }

            throw new SQLException("Impossible to get sensors values for the user with ID = " + userId + " from the DB.");
            //To change body of generated methods, choose Tools | Templates.
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplemetingClass.class.getName()).log(Level.SEVERE, null, ex);
        }


        return listOfCurrentValuesForExactUserToReturn;
        //throw new UnsupportedOperationException("Not supported yet."); 
        //To change body of generated methods, choose Tools | Templates.
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
    public List<Integer> getUserSensors(int userId) {
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


