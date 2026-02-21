package org.tcontrol.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.Logging;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.ICSVDataSaver;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.tcontrol.service.impl.CSVOptions.*;

@Component
@Slf4j
@Logging
public class CSVStatisticSaver implements ICSVDataSaver {

    @Value("${dataFile}")
    private String dataFileName;

    @Override
    public void write(List<? extends SensorValueShort> values) {
        boolean fileExist = Files.exists(Paths.get(dataFileName));
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(dataFileName, true), EXCEL)) {
            if (!fileExist) {
                printer.printRecord(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);
            }
            for (SensorValueShort value : values) {
                if (value != null) {
                    printer.printRecord(value.getSensorId(), value.getSensorCode(), value.getTimestamp(), value.getValue());
                    log.debug("saved: {}, {}, {}, {}", value.getSensorId(), value.getSensorCode(), value.getTimestamp(), value.getValue());
                }
            }
        } catch (IOException ex) {
            log.error("error while saving CSV", ex);
        }
    }

}
