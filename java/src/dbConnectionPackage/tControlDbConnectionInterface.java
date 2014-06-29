package dbConnectionPackage;

import java.util.*;
/**
 *
 * @author Anton Buslavskii
 */
public interface tControlDbConnectionInterface {
    
ArrayList<String> getSensors();
ArrayList<String> getValues(int SensorId, int startTimeMillis, int endTimeMillis);
ArrayList<String> addValues(int sensorId, double[] Values);
String addSensor(int Sensor);    
}