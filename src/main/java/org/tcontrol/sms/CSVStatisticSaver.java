package org.tcontrol.sms;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.service.IStatisticsSaver;
import org.tcontrol.service.models.SensorValue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CSVStatisticSaver implements IStatisticsSaver {

    @Value("${dataFile}")
    private String dataFile;

    @Override
    public void write(SensorValue[] values) {
        boolean fileExist = Files.exists(Paths.get(dataFile));
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(dataFile, true), CSVFormat.EXCEL)) {
            if (!fileExist) {
                printer.printRecord("sensorId", "timestamp", "value");
            }
            for (SensorValue value : values) {
                if (value != null) {
                    printer.printRecord(value.getSensorId(), value.getTimestamp(), value.getValue());
                }
            }
        } catch (IOException ex) {
            log.error("error while saving CSV", ex);
        }
    }

    @Override
    public Map<Integer, SensorValue> currentValues() {
        return Map.of();
    }

    @Override
    public LinkedList<SensorValue> lastValues(int sensorId) {
        return new LinkedList<>();
    }
}
