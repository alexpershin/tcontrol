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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
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
public class MySqlJdbcDaoTest {

    private static final Logger LOGGER = Logger.getLogger(MySqlJdbcDaoTest.class.getName());
    private static final String USER_PASSWORD = "";
    private static final String USER_LOGIN = "sa";

    private static DaoInterface dao;
    private static Server server;
    private static Connection connection;

    public MySqlJdbcDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        dao = new MySqlJDBCDaoImpl();
        connection = createH2Connection();
        // connection = createMySqlConnection();
        ((MySqlJDBCDaoImpl) dao).setDbConnection(connection);
        assertNotNull(((MySqlJDBCDaoImpl) dao).getDbConnection());
    }

    private static Connection createH2Connection() {
        Connection h2Connection = null;
        try {
            server = Server.createTcpServer(new String[]{}).start();

            Class.forName("org.h2.Driver");

            //TODO: disable storage in file:MV_STORE=FALSE and ;TRACE_LEVEL_SYSTEM_OUT=0
            h2Connection = DriverManager.getConnection("jdbc:h2:~/test", USER_LOGIN, USER_PASSWORD);

            //Create and fill H2 database for unit tests part
            Reader reader = new FileReader("sql-scripts/create-test-h2-tcontrol-db.sql");
            RunScript.execute(h2Connection, reader);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return h2Connection;
    }

    private static Connection createMySqlConnection() {
        Connection mysqlConnection = null;
        try {
            server = Server.createTcpServer(new String[]{}).start();

            //TODO set current chema: currentschema=MYDESIREDSCHEMA
            Class.forName("com.mysql.jdbc.Driver");
            mysqlConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost?"
                    + "user=testuser&password=testuser");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return mysqlConnection;
    }

    @AfterClass
    public static void tearDownClass() {
        if (server != null) {
            server.stop();
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MySqlJdbcDaoTest.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    connection = null;
                }
            }
            server = null;
        }
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAllSensorsTest() {
        Map<Integer, Sensor> sensorByIdMap = dao.getAllSensors();

        assertNotNull(sensorByIdMap);

        //check total count of sensors
        assertThat(sensorByIdMap.size(), is(6));

        //get one sensor and check that all it's field loaded correctly
        Sensor sensor1 = sensorByIdMap.get(1);
        assertNotNull(sensor1);
        assertThat(sensor1.getName(), is("Indoor"));
        assertThat(sensor1.getDescription(), is("Indoor Temperature Sensor"));
        assertThat(sensor1.getLowThreshold(), is(10.0));
        assertThat(sensor1.getHighThreshold(), is(30.0));
        assertThat(sensor1.getThresholdLag(), is(2.0));
        assertThat(sensor1.getType(), is(SensorType.TEMPERATURE));
    }

    @Test
    public void getCurrentValuesTest() {

        int userId = 2;
        List<SensorValue> sensorValues = dao.getCurrentValues(userId);

        assertNotNull(sensorValues);

        assertThat(sensorValues.size(), is(3));
        Map<Integer, SensorValue> sensorValueByIdMap = new HashMap<>();
        for (SensorValue sensorVl : sensorValues) {
            sensorValueByIdMap.put(sensorVl.getSensorId(), sensorVl);
        }
        assertThat(sensorValueByIdMap.size(), is(2));
        SensorValue sensorVl1 = sensorValueByIdMap.get(5);
        assertNotNull(sensorVl1);
        assertEquals(sensorVl1.getSensorId(), 5);

        //2014-08-11 15:16:17
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2014);
        c.set(Calendar.MONTH, 7);
        c.set(Calendar.DAY_OF_MONTH, 11);
        c.set(Calendar.HOUR_OF_DAY, 15);
        c.set(Calendar.MINUTE, 16);
        c.set(Calendar.SECOND, 17);
        c.setTimeZone(TimeZone.getDefault());
        c.set(Calendar.MILLISECOND, 0);

        assertEquals(sensorVl1.getTimestamp().getTime(), c.getTimeInMillis());

        assertEquals(sensorVl1.getValue(), 230.4, 0.05);
    }
}
