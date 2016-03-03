package com.tcontrol.sensormonitor;

import com.tcontrol.dao.SensorValue;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 */
@Stateless
@LocalBean
public class TemperatureMonitor {

    private static final Logger LOGGER = Logger.getLogger(TemperatureMonitor.class.getName());

    private static final String TEMPERATURE_PREFIX = "t=";

    private String w1DevicesPath = "/sys/bus/w1/devices";
    private String w1SensorInfoFileName = "w1_slave";

    public SensorValue loadTemperatureValue(String sensorUID) throws IOException {
        double t = readTemperatureFromFile(getFullPathToDevice(sensorUID));
        return new SensorValue(-1, System.currentTimeMillis(), t);
    }

    public Path getFullPathToDevice(String deviceFileName) {
        return FileSystems.getDefault().getPath(
                getW1DevicesPath() + "/" + deviceFileName 
                        + "/" + getW1SensorInfoFileName());
    }

    public static double readTemperatureFromFile(Path pathDeviceFile) throws IOException{
        int iniPos, endPos;
        String strTemp;
        double tvalue = 0;
        List<String> lines;
        try {
            lines = Files.readAllLines(pathDeviceFile, Charset.defaultCharset());
            for (String line : lines) {
                if (line.contains(TEMPERATURE_PREFIX)) {
                    iniPos = line.indexOf(TEMPERATURE_PREFIX) + 2;
                    endPos = line.length();
                    strTemp = line.substring(iniPos, endPos);
                    tvalue = Double.parseDouble(strTemp) / 1000;
                }
            }
        } catch (IOException ex) {
            LOGGER.log(SEVERE, "Error while reading file " + pathDeviceFile);
            throw ex;
        }
        return tvalue;
    }

    /**
     * @return the w1DevicesPath
     */
    public String getW1DevicesPath() {
        return w1DevicesPath;
    }

    /**
     * @param w1DevicesPath the w1DevicesPath to set
     */
    public void setW1DevicesPath(String w1DevicesPath) {
        this.w1DevicesPath = w1DevicesPath;
    }

    /**
     * @return the w1SensorInfoFileName
     */
    public String getW1SensorInfoFileName() {
        return w1SensorInfoFileName;
    }

    /**
     * @param w1SensorInfoFileName the w1SensorInfoFileName to set
     */
    public void setW1SensorInfoFileName(String w1SensorInfoFileName) {
        this.w1SensorInfoFileName = w1SensorInfoFileName;
    }
}
