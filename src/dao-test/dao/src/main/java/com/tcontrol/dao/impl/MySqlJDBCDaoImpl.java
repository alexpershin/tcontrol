/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcontrol.dao.impl;

import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.Sensor.SensorType;
import com.tcontrol.dao.SensorValue;
import com.tcontrol.dao.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.EnumUtils;

/**
 *
 * @author Anton Buslavskii
 */
public class MySqlJDBCDaoImpl implements DaoInterface {

    private static final Logger LOGGER = Logger.getLogger(MySqlJDBCDaoImpl.class.getName());
    private Connection dbConnection;

    @Override
    public List<Sensor> getAllSensors() {
        List<Sensor> listOfSensorsToReturn = new ArrayList<>();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            //TODO remove schema
            String selectSQL = "SELECT id, name, type, description, low_thresshold, "
                    + "high_thresshold, threshold_delta from dbtcontrol.sensors";

            statement = getDbConnection().createStatement();

            resultSet = statement.executeQuery(selectSQL);

            while (resultSet.next()) {
                //extract sensor's fields
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                SensorType type = EnumUtils.getEnum(SensorType.class, resultSet.getString("type"));
                double lowThresshold = resultSet.getDouble("low_thresshold");
                double highThresshold = resultSet.getDouble("high_thresshold");
                double thressholdDelta = resultSet.getDouble("threshold_delta");

                //build sensor object
                Sensor sensor = new Sensor(id, name, type, null, description);
                sensor.setLowThreshold(lowThresshold);
                sensor.setHighThreshold(highThresshold);
                sensor.setThresholdDelta(thressholdDelta);

                //add sensor to result list
                listOfSensorsToReturn.add(sensor);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                resultSet = null;
            }

            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                statement = null;
            }
        }

        return listOfSensorsToReturn;
    }

    @Override
    public List<Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(int userId) {
        List<SensorValue> listOfSensorValuesToReturnForTheUser = new ArrayList<>();
        //Statement statement = null; //We'll use a PreparedStatement
        ResultSet resultSet = null;
        PreparedStatement preparedStatement;

        try {

            String selectSQL = "SELECT value from dbtcontrol.sensor_values"
                    + "where sensor_id in "
                    + "(select sensor_id from dbtcontrol.profiles"
                    + "where user_id = ?)";
            //here must be selected max of timestamp either

            
            preparedStatement = dbConnection.prepareStatement(selectSQL);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery(selectSQL);

            //TODO remove schema
            while (rs.next()) {
                
                double value = rs.getDouble("value");

                //build sensorValue object
                SensorValue sensorsValue = new SensorValue(value);
                sensorsValue.setValue(value);

                listOfSensorValuesToReturnForTheUser.add(sensorsValue);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                resultSet = null;
            }

//            try {
//                if (preparedStatement != null) {
//                    preparedStatement.close();
//                }
//            } catch (SQLException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } finally {
//                preparedStatement = null;
//            }
        }

        return listOfSensorValuesToReturnForTheUser;

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    /**
     * TODO use connection pool here instead of connection instance
     * @return the dbConnection
     */
    public Connection getDbConnection() {
        return dbConnection;
    }

    /**
     * @param dbConnection the dbConnection to set
     */
    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

}
