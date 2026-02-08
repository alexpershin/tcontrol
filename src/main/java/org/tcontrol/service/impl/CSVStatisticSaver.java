package org.tcontrol.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.Logging;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.models.SensorValue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.tcontrol.service.impl.CSVOptions.*;

@Component
@Slf4j
@Logging
public class CSVStatisticSaver implements IStatisticsSaver {

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
                }
            }
        } catch (IOException ex) {
            log.error("error while saving CSV", ex);
        }
    }

    @Override
    public Map<Integer, SensorValue> currentValues() {
        return new HashMap<>();
    }

    @Override
    public LinkedList<SensorValueShort> lastValues(int sensorId) {
        return new LinkedList<>();
    }

    @Override
    public Collection<SensorValueShort> historyValues(long from, long to, Integer sensorId) {
        return Collections.emptyList();
    }
}
