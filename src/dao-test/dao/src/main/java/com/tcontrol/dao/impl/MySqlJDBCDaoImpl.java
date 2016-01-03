package com.tcontrol.dao.impl;

import com.tcontrol.dao.DaoException;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import org.apache.commons.lang3.EnumUtils;

/**
 * Implementation of real database DAO.
 */
@Stateless
public class MySqlJDBCDaoImpl implements DaoInterface {

    private static final Logger LOGGER = Logger.getLogger(MySqlJDBCDaoImpl.class.getName());

    /**
     * Data source.
     */
    private DataSource dataSource;

    @Override
    public Map<Integer, Sensor> getAllSensors() throws DaoException {

        Map<Integer, Sensor> listOfSensorsToReturn = new HashMap<>();
        //TODO remove schema
        String selectSQL = "SELECT id, name, type, description, low_thresshold, "
                + "high_thresshold, threshold_delta from dbtcontrol.sensors";

        try (Connection connection = getDataSource().getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectSQL);) {

            extractSensorFromResultSet(resultSet, listOfSensorsToReturn);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new DaoException(ex);
        }
        return listOfSensorsToReturn;
    }

    private void extractSensorFromResultSet(final ResultSet resultSet, Map<Integer, Sensor> listOfSensorsToReturn) throws SQLException {
        while (resultSet.next()) {
            //extract sensor's fields
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            final String typeString = resultSet.getString("type");
            SensorType type = EnumUtils.getEnum(SensorType.class, typeString);
            double lowThresshold = resultSet.getDouble("low_thresshold");
            double highThresshold = resultSet.getDouble("high_thresshold");
            double thressholdDelta = resultSet.getDouble("threshold_delta");

            //build sensor object
            Sensor sensor = new Sensor(id, name, type, description);
            sensor.setLowThreshold(lowThresshold);
            sensor.setHighThreshold(highThresshold);
            sensor.setThresholdLag(thressholdDelta);

            //add sensor to result list
            listOfSensorsToReturn.put(sensor.getId(), sensor);
        }
    }

    @Override
    public Map<Integer, Sensor> getSensors(List<Integer> ids) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SensorValue> getCurrentValues(final int userId) {
        final List<SensorValue> listOfSensorValues = new ArrayList<>();

        try (Connection connection = getDataSource().getConnection();
                PreparedStatement preparedStatement
                = createPreparedStatementToGetCurrentValues(connection, userId);
                ResultSet resultSet = preparedStatement.executeQuery();) {

            while (resultSet.next()) {

                double value = resultSet.getDouble("value");
                Timestamp timeStamp = resultSet.getTimestamp("timestamp");
                int sensorId = resultSet.getInt("sensor_id");

                //build sensorValue object
                SensorValue sensorsValue = new SensorValue(value);
                sensorsValue.setValue(value);
                sensorsValue.setTimestamp(timeStamp);
                sensorsValue.setSensorId(sensorId);

                listOfSensorValues.add(sensorsValue);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return listOfSensorValues;
    }

    private PreparedStatement createPreparedStatementToGetCurrentValues(
            Connection con,
            int userId) throws SQLException {
        //TODO remove schema
        final String sql = "select "
                + "v.sensor_id sensor_id,"
                + "v.timestamp timestamp,"
                + "v.value value"
                + " from dbtcontrol.sensor_values v"
                + " where v.id in ("
                + "  select max(vl.id) id"
                + "   from "
                + "     dbtcontrol.sensor_values vl, "
                + "     dbtcontrol.profiles p"
                + "   where vl.sensor_id = p.sensor_id"
                + "   and p.user_id = ?"
                + "   group by vl.sensor_id"
                + ")";

        final PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, userId);
        return ps;
    }

    @Override
    public void addValues(int sensorId, ArrayList<SensorValue> values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer addSensor(String sensorType) {
        throw new UnsupportedOperationException("Not supported yet.");
        //VERY STRANGE CODE and without UNIT tests, most probably it would work correctly.
//        int addedSensorId = 0;
//        ResultSet resultSet = null;
//        PreparedStatement preparedStatement;
//        double intermediateRandom = (Math.random() * 10000);
//        int randomFigureToAvoidDoublingOfSensors = (int) Math.round(intermediateRandom);
//
//        try {
//
//            int currentTimeInMillisWithRandom = (int) java.lang.System.currentTimeMillis() + randomFigureToAvoidDoublingOfSensors;
//            String selectSQL = "INSERT INTO dbtcontrol.sensors (name, type, description ) "
//                    + "VALUES ('newly added sensor', 'type example', ?);"
//                    + "SELECT id "
//                    + "from dbtcontrol.sensors "
//                    + "WHERE description = ?";
//
//            preparedStatement
//                    = getDataSource().getConnection().prepareStatement(selectSQL);
//            preparedStatement.setInt(1, currentTimeInMillisWithRandom);
//            preparedStatement.setInt(2, currentTimeInMillisWithRandom);
//            ResultSet rs = preparedStatement.executeQuery(selectSQL);
//
//            int sensorIdToReturn = rs.getInt("id");
//
//            addedSensorId = sensorIdToReturn;
//
//        } catch (SQLException ex) {
//            LOGGER.log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                if (resultSet != null) {
//                    resultSet.close();
//                }
//            } catch (SQLException ex) {
//                LOGGER.log(Level.SEVERE, null, ex);
//            } finally {
//                resultSet = null;
//            }
//
//        }
//
//        return addedSensorId;
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
    public Map<Integer, Sensor> getUserSensors(int userId) throws DaoException {
        Map<Integer, Sensor> listOfSensorsToReturn = new HashMap<>();

        try (
                Connection connection = getDataSource().getConnection();
                PreparedStatement statement
                = createPreparedStatementToGetUserSensors(connection, userId);
                ResultSet resultSet
                = statement.executeQuery();) {

            extractSensorFromResultSet(resultSet, listOfSensorsToReturn);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new DaoException(ex);
        }
        return listOfSensorsToReturn;
    }

    private PreparedStatement createPreparedStatementToGetUserSensors(
            Connection con,
            int userId) throws SQLException {
        //TODO remove schema
        String sql
                = "SELECT s.id id, s.name name, s.type type, "
                + "s.description description, s.low_thresshold low_thresshold, "
                + "s.high_thresshold high_thresshold, s.threshold_delta threshold_delta"
                + " from dbtcontrol.sensors s, dbtcontrol.profiles p"
                + " where s.id = p.sensor_id"
                + "   and p.user_id = ?";

        final PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, userId);
        return ps;
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
     * @return the dataSource;
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set;
     */
    @Resource(mappedName = "jdbc/tcontrol-db")
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
