package org.tcontrol.hardware.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tcontrol.config.Logging;
import org.tcontrol.models.SensorValue;
import org.tcontrol.hardware.ITemperatureReader;
import org.tcontrol.service.SystemAlertsAggregator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
@Logging
@RequiredArgsConstructor
public class TemperatureReader implements ITemperatureReader {

    private static final String TEMPERATURE_PREFIX = "t=";
    private static final String CRC_PREFIX = "crc=";
    private static final String CRC_OK = "YES";

    private final SystemAlertsAggregator systemAlertsAggregator;

    @Value("${w1DevicesPath}")
    private String w1DevicesPath;

    @Value("${w1SensorInfoFileName:#{'w1_slave'}}")
    private String w1SensorInfoFileName;

    @Override
    public SensorValue getValue(String sensorCode)  {
        double t = 0;
        try {
            t = readTemperatureFromFile(sensorCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new SensorValue(null, System.currentTimeMillis(), t);
    }

    private Path getFullPathToDevice(String sensorCode) {
        return FileSystems.getDefault().getPath(
                getW1DevicesPath() + "/" + sensorCode
                        + "/" + getW1SensorInfoFileName());
    }

    private double readTemperatureFromFile(String sensorCode) throws IOException {
        Path pathDeviceFile = getFullPathToDevice(sensorCode);
        log.debug("Read temperature from file: {}", pathDeviceFile.toString());
        int iniPos, endPos;
        String strTemp;
        double tValue = 0;
        List<String> lines;
        try {
            lines = Files.readAllLines(pathDeviceFile, Charset.defaultCharset());
            for (String line : lines) {
                if (line.contains(TEMPERATURE_PREFIX)) {
                    iniPos = line.indexOf(TEMPERATURE_PREFIX) + 2;
                    endPos = line.length();
                    strTemp = line.substring(iniPos, endPos);
                    tValue = Double.parseDouble(strTemp) / 1000;
                    log.debug("Value: {}", tValue);
                } else if (line.contains(CRC_PREFIX)) {
                    String crcStatus = line.substring(line.lastIndexOf(" ") + 1);
                    log.debug("CRC: {}", crcStatus);
                    if (!CRC_OK.equals(crcStatus)) {
                        String errorMessage = MessageFormat.format("CRC for sensor {0} is bad", sensorCode);
                        systemAlertsAggregator.addAlert(errorMessage);
                        log.error(errorMessage, pathDeviceFile.getFileName());
                        throw new IOException(errorMessage);
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Error while reading file " + pathDeviceFile);
            throw ex;
        }
        return tValue;
    }

    private String getW1DevicesPath() {
        return w1DevicesPath;
    }

    private String getW1SensorInfoFileName() {
        return w1SensorInfoFileName;
    }
}

