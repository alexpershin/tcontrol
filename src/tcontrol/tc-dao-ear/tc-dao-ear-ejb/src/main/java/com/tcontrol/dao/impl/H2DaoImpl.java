package com.tcontrol.dao.impl;

import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.Sensor.SensorType;
import com.tcontrol.dao.SensorValue;
import com.tcontrol.dao.User;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Implementation of real database DAO.
 */
@Stateless
public class H2DaoImpl implements DaoInterface {

    private static final Logger LOGGER = Logger.getLogger(H2DaoImpl.class.getName());

    private static final int DATA_LOAD_TIME_INTERVAL = 135;
    private static final int GRADIENT_HIGH_TIME_INTERVAL = 75;
    private static final int GRADIENT_LOW_TIME_INTERVAL = 45;

    /**
     * Data source.
     */
    private DataSource dataSource;

    @Override
    public Map<Integer, Sensor> getAllSensors() throws DaoException {

        Map<Integer, Sensor> listOfSensorsToReturn = new HashMap<>();
        //TODO remove schema
        String selectSQL = "SELECT id, serial_number, name, type, description, low_thresshold, "
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
            String serialNumber = resultSet.getString("serial_number");
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
            sensor.setSerialNumber(serialNumber);

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
        return getCurrentValues(userId, new Date(System.currentTimeMillis()));
    }

    @Override
    public List<SensorValue> getCurrentValues(final int userId, Date date) {
        final List<SensorValue> listOfSensorValues = new ArrayList<>();
        try (Connection connection = getDataSource().getConnection();
                PreparedStatement maxIDresultSetPrepStatement
                = createPreparedStatementToGetMaxId(connection);
                ResultSet maxIDresultSet = maxIDresultSetPrepStatement.executeQuery();
                PreparedStatement numOfSensorsPrepStatement
                = createPreparedStatementToGetNumberOfSensors(connection);
                ResultSet sensorsNumresultSet = numOfSensorsPrepStatement.executeQuery();) {
            Integer maxIndex = null;
            Integer numOfSensors = null;
            if (maxIDresultSet.next()) {
                maxIndex = maxIDresultSet.getInt(1);
            }
            if (sensorsNumresultSet.next()) {
                numOfSensors = sensorsNumresultSet.getInt(1);
            }
            if (maxIndex != null && numOfSensors != null) {
                double gradientPeriod = 1.0;//1hour
                double rate = 0.25;//15мин
                double numOfValues = (gradientPeriod / rate + 1) * numOfSensors;
                numOfValues = maxIndex >= numOfValues ? numOfValues : maxIndex;
                Integer[] indexes = new Integer[(new Double(Math.floor(numOfValues))).intValue()];
                for (int i = 0; i < indexes.length; i++) {
                    indexes[i] = maxIndex - i;
                }
                try (
                        PreparedStatement gradientStatement
                        = createPreparedStatementToGetLastHourGradientValues(connection, userId, indexes, date);
                        ResultSet gradientSet = gradientStatement.executeQuery();) {

                    Map<Integer, SensorValue> valuesMap = new HashMap<>();
                    final Timestamp startGradientLoadDate
                            = new Timestamp(date.getTime() - GRADIENT_HIGH_TIME_INTERVAL * 60 * 1000);
                    final Map<Integer, MutablePair<SensorValue, SensorValue>> gradMap = new HashMap<>();
                    while (gradientSet.next()) {
                        double value = gradientSet.getDouble("value");
                        Timestamp timeStamp = gradientSet.getTimestamp("timestamp");
                        int sensorId = gradientSet.getInt("sensor_id");

                        MutablePair<SensorValue, SensorValue> pair = gradMap.get(sensorId);

                        SensorValue sValue = new SensorValue(value);
                        sValue.setTimestamp(timeStamp);
                        sValue.setSensorId(sensorId);

                        if (timeStamp.compareTo(startGradientLoadDate) >= 0) {
                            if (pair == null) {
                                pair = new MutablePair<>(sValue, null);
                                gradMap.put(sensorId, pair);
                            } else {
                                pair.setRight(sValue);
                            }
                        }

                        if (!valuesMap.containsKey(sensorId)
                                || valuesMap.get(sensorId).getTimestamp().compareTo(timeStamp) < 0) {
                            valuesMap.put(sensorId, sValue);
                        }
                    }

                    valuesMap.entrySet().forEach(e -> listOfSensorValues.add(e.getValue()));

                    gradMap.entrySet().forEach(e -> {
                        int sensorId = e.getKey();
                        MutablePair<SensorValue, SensorValue> pair = gradMap.get(sensorId);
                        if (pair != null) {
                            SensorValue firstValue = pair.getLeft();
                            SensorValue lastValue = pair.getValue();

                            if (firstValue != null && lastValue != null) {
                                double diff = lastValue.getTimestamp().getTime() - firstValue.getTimestamp().getTime();
                                if (diff > GRADIENT_LOW_TIME_INTERVAL * 60 * 1000
                                        && diff < GRADIENT_HIGH_TIME_INTERVAL * 60 * 1000) {
                                    Double gradientValue = (lastValue.getValue() - firstValue.getValue()) * 3600000.0 / diff;
                                    SensorValue sensorValue = valuesMap.get(sensorId);
                                    sensorValue.setGradient(gradientValue);
                                }
                            }
                        }
                    });

                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return listOfSensorValues;
    }

    private PreparedStatement createPreparedStatementToGetMaxId(
            Connection con) throws SQLException {

        final String sql = "select max(id) from dbtcontrol.sensor_values";

        final PreparedStatement ps = con.prepareStatement(sql);
        return ps;
    }

    private PreparedStatement createPreparedStatementToGetNumberOfSensors(
            Connection con) throws SQLException {

        final String sql = "select count(*) from dbtcontrol.sensors";

        final PreparedStatement ps = con.prepareStatement(sql);
        return ps;
    }

    private PreparedStatement createPreparedStatementToGetLastHourGradientValues(
            Connection con,
            int userId,
            Integer[] ids,
            Date date) throws SQLException {
//query before optimization
//        final String sql = "select "
//                + "v.sensor_id sensor_id,"
//                + "v.timestamp timestamp,"
//                + "v.value value"
//                + " from dbtcontrol.sensor_values v"
//                + " where v.id in ("
//                + "  select vl.id"
//                + "   from "
//                + "     dbtcontrol.sensor_values vl, "
//                + "     dbtcontrol.profiles p"
//                + "   where vl.sensor_id = p.sensor_id"
//                + "   and p.user_id = ?"
//                + "   and vl.timestamp >= ?"
//                + ")"
//                + "order by sensor_id, timestamp";
        String sql = "select"
                + " id"
                + ",sensor_id"
                + ",timestamp"
                + ",value"
                + " from dbtcontrol.sensor_values where sensor_id in("
                + " select sensor_id from dbtcontrol.profiles where user_id=?)"
                + " and id in (%s) and timestamp >= ? order by sensor_id, timestamp";
        
        //Doesn't support by hd2db
        //Array array = ps.getConnection().createArrayOf("BIGINT", ids);
        //ps.setArray(2, array);
        //ps.setString(2, array);

        String array = Arrays.stream(ids).map(i -> i.toString()).collect(Collectors.joining(","));
        sql = String.format(sql, array);

        final PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, userId);

        final Timestamp startDate
                = new Timestamp(date.getTime() - DATA_LOAD_TIME_INTERVAL * 60 * 1000);
        ps.setTimestamp(2, startDate);

        return ps;
    }

    @Override
    public void addValues(List<SensorValue> values) {
        try (Connection connection = getDataSource().getConnection();) {

            for (SensorValue value : values) {
                try (
                        PreparedStatement preparedStatement
                        = createPreparedStatementToSaveValue(connection, value);) {
                    preparedStatement.execute();

                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            LOGGER.log(Level.INFO, String.format("%s values added.", values.size()));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Integer addSensor(String sensorType) {
        throw new UnsupportedOperationException("Not supported yet.");
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
                = "SELECT s.id id, s.serial_number serial_number, s.name name, s.type type, "
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

    /**
     * Creates statement to save sensor value to the database.
     *
     * @param connection connection;
     * @param value the value;
     * @return prepared statement;
     */
    private PreparedStatement createPreparedStatementToSaveValue(
            Connection connection,
            SensorValue value) throws SQLException {
        //TODO remove schema
        String sql = "INSERT INTO dbtcontrol.sensor_values(sensor_id,timestamp,value)"
                + "values(?,?,?)";

        final PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, value.getSensorId());
        ps.setTimestamp(2, value.getTimestamp());
        ps.setDouble(3, value.getValue());
        return ps;
    }
}
