package dbConnectionPackage;

import java.util.ArrayList;
//TODO: add PreparedStatement for each method and uncomment the following import statement
//import java.sql.PreparedStatement;

/**
 *
 * @author Anton Buslavskii
 */
public class tControlDbConnectionClass implements tControlDbConnectionInterface {

    @Override
    public String addSensor(int Sensor) {
        //TODO: add PreparedStatement for each 
        //PreparedStatement prStmntAddSensor;
        //prStmntAddSensor = 
        throw new  UnsupportedOperationException("Error in adding of Sensor."); 
    }

    @Override
    public ArrayList<String> addValues(int sensorId, double[] Values) {
        
        throw new UnsupportedOperationException("Error in adding of values of sensor(s)."); 
    }

    @Override
    public ArrayList<String> getSensors(int[] Sensors) {
        throw new UnsupportedOperationException("Error in getting of Sensors."); 
    }

    @Override
    public ArrayList<String> getValues(int SensorId, int startTimeMillis, int endTimeMillis) {
        throw new UnsupportedOperationException("Error in getting of Values."); 
                //To change body of generated methods, choose Tools | Templates.
    }
    
}