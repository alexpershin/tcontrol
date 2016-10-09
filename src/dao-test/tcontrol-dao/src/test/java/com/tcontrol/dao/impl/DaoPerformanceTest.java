package com.tcontrol.dao.impl;

import com.tcontrol.dao.DaoException;
import com.tcontrol.dao.DaoInterface;
import com.tcontrol.dao.Sensor;
import com.tcontrol.dao.SensorValue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.h2.store.fs.FileUtils;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import static org.hamcrest.CoreMatchers.is;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaoPerformanceTest {

    private static final Logger LOGGER = Logger.getLogger(DaoPerformanceTest.class.getName());
    private static final String USER_PASSWORD = "";
    private static final String USER_LOGIN = "sa";

    private static DaoInterface dao;
    private static Server server;

    public DaoPerformanceTest() {
    }

    @BeforeClass
    public static void setUpClass() throws SQLException {
        dao = new MySqlJDBCDaoImpl();
        LOGGER.log(Level.INFO, "Remove database...");
        FileUtils.delete("~/unittestdb.mv.db");
        FileUtils.delete("~/unittestdb.trace.db");
        
        initDb();
        
        DataSource dataSource = mock(DataSource.class);
        ((MySqlJDBCDaoImpl) dao).setDataSource(dataSource);
        assertNotNull(((MySqlJDBCDaoImpl) dao).getDataSource());
    }

    private static Connection createH2Connection() throws SQLException {
        Connection h2Connection = null;
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
                LOGGER.log(Level.INFO, "Creating database start...");
                Reader reader = new FileReader("sql-scripts/tcontrol-db.h2-2016-09-30.sql");
                RunScript.execute(h2Connection, reader);
                LOGGER.log(Level.INFO, "Creating database complete.");

                return h2Connection;
            } catch (SQLException | FileNotFoundException ex) {
                LOGGER.log(Level.INFO, null, ex);

            }
        } catch (SQLException | ClassNotFoundException ex) {
            LOGGER.log(Level.INFO, null, ex);
        }
        return null;
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
        reinitConnection();
    }
    
    private void reinitConnection() throws SQLException {
        DataSource dataSource = ((MySqlJDBCDaoImpl) dao).getDataSource();
        when(dataSource.getConnection()).thenReturn(createH2Connection());
    }

    @Test
    public void getUserSensorsTest() throws DaoException {
        long t1 = System.currentTimeMillis();
        Map<Integer, Sensor> sensorByIdMap = dao.getUserSensors(1);
        assertThat(sensorByIdMap.size(), is(4));
        long t2 = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "getUserSensors: " + (t2 - t1) +"msec");
    }

    @Test
    public void getCurrentValuesTest() throws DaoException {
        long t1 = System.currentTimeMillis();
        List<SensorValue> values = dao.getCurrentValues(1);
        long t2 = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "getCurrentValues: " + (t2 - t1) +"msec");
        assertThat(values.size(), is(4));
    }

}
