package com.tcontrol.dao.impl;

import com.tcontrol.dao.DaoException;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class MySqlJdbcDaoTest {

    private static final String YYYY_M_MDD_H_HMMSS = "yyyy-MM-dd HH:mm:ss";

    private static final Logger LOGGER = Logger.getLogger(MySqlJdbcDaoTest.class.getName());
    private static final String USER_PASSWORD = "";
    private static final String USER_LOGIN = "sa";

    private static DaoInterface dao;
    private static Server server;
    private static Connection connection;

    public MySqlJdbcDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        dao = new H2DaoImpl();

        DataSource dataSource = mock(DataSource.class);
        ((H2DaoImpl) dao).setDataSource(dataSource);
        assertNotNull(((H2DaoImpl) dao).getDataSource());
    }

    private static Connection createH2Connection() throws SQLException {
        Connection h2Connection = null;
        //TODO: disable storage in file:MV_STORE=FALSE and ;TRACE_LEVEL_SYSTEM_OUT=0
        //Use memory mode - 'jdbc:h2:mem' 
        h2Connection = DriverManager.getConnection("jdbc:h2:~/unittestdb", USER_LOGIN, USER_PASSWORD);
        return h2Connection;
    }

    private static Connection initDb() {
        try {
            server = Server.createTcpServer(new String[]{}).start();

            Class.forName("org.h2.Driver");

            Connection h2Connection = createH2Connection();
            try {

                //Create and H2 database for unit tests part
                Reader reader = new FileReader("sql-scripts/create-schema-h2-tcontrol-db.sql");
                RunScript.execute(h2Connection, reader);

                //Load test data to the database.
                reader = new FileReader("sql-scripts/create-test-data-h2-tcontrol-db.sql");
                RunScript.execute(h2Connection, reader);

                return h2Connection;
            } catch (SQLException | FileNotFoundException ex) {
                LOGGER.log(Level.SEVERE, null, ex);

            }
        } catch (SQLException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
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
        } catch (SQLException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return mysqlConnection;
    }

    @AfterClass
    public static void tearDownClass() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

    @Before
    public void setUp() throws SQLException {
        DataSource dataSource = ((H2DaoImpl) dao).getDataSource();

        final Connection connection = initDb();
        //connection = createMySqlConnection();

        when(dataSource.getConnection()).thenReturn(connection);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void getAllSensorsTest() throws DaoException {
        Map<Integer, Sensor> sensorByIdMap = dao.getAllSensors();

        assertNotNull(sensorByIdMap);

        //check total count of sensors
        assertThat(sensorByIdMap.size(), is(6));

        //get one sensor and check that all it's field loaded correctly
        Sensor sensor1 = sensorByIdMap.get(1);
        assertNotNull(sensor1);
        assertThat(sensor1.getSerialNumber(), is("28-000000abc"));
        assertThat(sensor1.getName(), is("Indoor"));
        assertThat(sensor1.getDescription(), is("Indoor Temperature Sensor"));
        assertThat(sensor1.getLowThreshold(), is(10.0));
        assertThat(sensor1.getHighThreshold(), is(30.0));
        assertThat(sensor1.getThresholdLag(), is(2.0));
        assertThat(sensor1.getType(), is(SensorType.TEMPERATURE));
    }

    @Test
    public void getCurrentValuesTestUser2() throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat(YYYY_M_MDD_H_HMMSS);
        final Date date = format.parse("2014-10-09 11:01:00");

        int userId = 2;
        List<SensorValue> sensorValues = dao.getCurrentValues(userId, new java.sql.Date(date.getTime()));

        assertNotNull(sensorValues);

        assertThat(sensorValues.size(), is(1));
        //build map sensorId->value
        Map<Integer, SensorValue> sensorValueByIdMap = new HashMap<>();
        sensorValues.stream().forEach((sensorVl) -> {
            sensorValueByIdMap.put(sensorVl.getSensorId(), sensorVl);
        });

        assertThat(sensorValueByIdMap.size(), is(1));

        //check value 1
//        SensorValue sensorVl1 = sensorValueByIdMap.get(5);
//        checkValue(sensorVl1, 5L, "2014-08-11 15:16:17", 230.6);
        //check value 2
        SensorValue sensorVl2 = sensorValueByIdMap.get(3);
        checkValue(sensorVl2, 3L, "2014-10-09 11:01:17", 1.0);
    }

    @Test
    public void getCurrentValuesTestUser1() throws ParseException {

        final int userId = 1;
        final SimpleDateFormat format = new SimpleDateFormat(YYYY_M_MDD_H_HMMSS);
        final Date date = format.parse("2014-08-12 12:00:00");
        List<SensorValue> sensorValues = dao.getCurrentValues(userId, new java.sql.Date(date.getTime()));

        assertNotNull(sensorValues);

        assertThat(sensorValues.size(), is(2));
        Map<Integer, SensorValue> sensorValueByIdMap = new HashMap<>();
        sensorValues.stream().forEach((sensorVl) -> {
            sensorValueByIdMap.put(sensorVl.getSensorId(), sensorVl);
        });

        assertThat(sensorValueByIdMap.size(), is(2));

        //check value 1
        SensorValue sensorVl1 = sensorValueByIdMap.get(1);
        checkValue(sensorVl1, 1L, "2014-08-12 12:00:00", 21.5);

        //gradient could be calculated
        assertThat(sensorVl1.getGradient(), closeTo(0.3157, 0.001));

        //check value 2
        SensorValue sensorVl2 = sensorValueByIdMap.get(2);
        checkValue(sensorVl2, 2L, "2014-08-12 12:00:00", 8.56);

        //no gradien for the period
        assertNull(sensorVl2.getGradient());
    }

    private void checkValue(
            final SensorValue sensorVl1,
            final long expectedSensorId,
            final String expectedTime,
            final double expectedValue)
            throws ParseException {

        final SimpleDateFormat format = new SimpleDateFormat(YYYY_M_MDD_H_HMMSS);
        final Date date = format.parse(expectedTime);
        checkValue(sensorVl1, expectedSensorId, date, expectedValue);
    }

    private void checkValue(
            final SensorValue sensorVl1,
            final long expectedSensorId,
            final Date expectedTime,
            final double expectedValue)
            throws ParseException {

        //check sensor id
        assertNotNull(sensorVl1);
        assertEquals(sensorVl1.getSensorId(), expectedSensorId);

        //check value date
        assertEquals(sensorVl1.getTimestamp().getTime(), expectedTime.getTime());

        //check value
        final double delta = 0.05;
        assertEquals(sensorVl1.getValue(), expectedValue, delta);
    }

    @Test
    public void getUserSensorsTest() throws DaoException {
        Map<Integer, Sensor> sensorByIdMap = dao.getUserSensors(2);
        assertNotNull(sensorByIdMap);

        //check total count of sensors
        assertThat(sensorByIdMap.size(), is(3));

        //get one sensor and check that all it's field loaded correctly
        //(5, 'Power Voltage', 'VOLTAGE', 205, 255, 5, 'Power Grid Voltage');
        {
            Sensor sensor1 = sensorByIdMap.get(5);
            assertNotNull(sensor1);
            assertThat(sensor1.getName(), is("Power Voltage"));
            assertThat(sensor1.getSerialNumber(), is("28-000003abc"));
            assertThat(sensor1.getDescription(), is("Power Grid Voltage"));
            assertThat(sensor1.getLowThreshold(), is(205.0));
            assertThat(sensor1.getHighThreshold(), is(255.0));
            assertThat(sensor1.getThresholdLag(), is(5.0));
            assertThat(sensor1.getType(), is(SensorType.VOLTAGE));
        }
        //( 'test_sensor', 'TEMPERATURE', 10, 30, 2, 'Indoor Temperature Sensor')
        {
            Sensor sensor1 = sensorByIdMap.get(6);
            assertNotNull(sensor1);
            assertThat(sensor1.getName(), is("28-000006abc"));
            assertThat(sensor1.getDescription(), is("Indoor Temperature Sensor"));
            assertThat(sensor1.getLowThreshold(), is(10.0));
            assertThat(sensor1.getHighThreshold(), is(30.0));
            assertThat(sensor1.getThresholdLag(), is(2.0));
            assertThat(sensor1.getType(), is(SensorType.TEMPERATURE));
        }
        //(3, 'Power', 'ON_OF', 'Power On or Off Sensor')
//        {
//            Sensor sensor1 = sensorByIdMap.get(3);
//            assertNotNull(sensor1);
//            assertThat(sensor1.getName(), is("Power"));
//            assertThat(sensor1.getDescription(), is("Power On or Off Sensor"));
//            assertThat(sensor1.getType(), is(SensorType.ON_OFF));
//        }
    }

    @Test
    public void saveValueTest() throws ParseException, SQLException {

        int userId = 1;
        final long currentTimeMillis = System.currentTimeMillis();
        dao.addValues(Arrays.asList(new SensorValue(
                1,
                new Timestamp(currentTimeMillis),
                25.5
        )));

        reinitConnection();

        List<SensorValue> sensorValues = dao.getCurrentValues(userId);

        assertNotNull(sensorValues);

        assertThat(sensorValues.size(), is(1));
        //build map sensorId->value
        Map<Integer, SensorValue> sensorValueByIdMap = new HashMap<>();
        sensorValues.stream().forEach((sensorVl) -> {
            sensorValueByIdMap.put(sensorVl.getSensorId(), sensorVl);
        });

        assertThat(sensorValueByIdMap.size(), is(1));

        //check value 1
        SensorValue sensorVl1 = sensorValueByIdMap.get(1);
        checkValue(sensorVl1, 1L, new Timestamp(currentTimeMillis), 25.5);
    }

    @Test
    public void saveValuesTest() throws ParseException, SQLException {

        int userId = 1;
        final long currentTimeMillis = System.currentTimeMillis();
        dao.addValues(Arrays.asList(
                new SensorValue(
                        1,
                        new Timestamp(currentTimeMillis),
                        25.5
                ),
                new SensorValue(
                        1,
                        new Timestamp(currentTimeMillis + 1000000L),
                        26.5
                )));

        reinitConnection();

        List<SensorValue> sensorValues = dao.getCurrentValues(userId);

        assertNotNull(sensorValues);

        assertThat(sensorValues.size(), is(1));
        //build map sensorId->value
        Map<Integer, SensorValue> sensorValueByIdMap = new HashMap<>();
        sensorValues.stream().forEach((sensorVl) -> {
            sensorValueByIdMap.put(sensorVl.getSensorId(), sensorVl);
        });

        assertThat(sensorValueByIdMap.size(), is(1));

        //check value 2
        SensorValue sensorVl1 = sensorValueByIdMap.get(1);
        checkValue(sensorVl1, 1L, new Timestamp(currentTimeMillis + 1000000L), 26.5);
    }

    private void reinitConnection() throws SQLException {
        DataSource dataSource = ((H2DaoImpl) dao).getDataSource();
        when(dataSource.getConnection()).thenReturn(createH2Connection());
    }
}
