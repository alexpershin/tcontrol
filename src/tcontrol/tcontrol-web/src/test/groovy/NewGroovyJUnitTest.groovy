import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*

import com.tcontrol.dao.Sensor
import static com.tcontrol.dao.Sensor.SensorType.TEMPERATURE
import com.tcontrol.dao.SensorValue;
import com.tcontrol.webservice.sensor.SensorsWebService
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.ALERT
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.NORMAL
import static com.tcontrol.webservice.sensor.SensorValueWeb.SensorValueState.WARNING

class NewGroovyJUnitTest {

    public NewGroovyJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void hello() {
        assert true
    }
    
    @Test
    public void test1(){
        def id = 1
        def time = System.currentTimeMillis()
        def sensorValue=new SensorValue (id, time, 20.0);
        def sensor =   new Sensor (id, "Indoor", TEMPERATURE, "", 10, 30, 2)
        
        assert SensorsWebService.calculateSate(sensorValue, sensor)== NORMAL
    }
    
}
