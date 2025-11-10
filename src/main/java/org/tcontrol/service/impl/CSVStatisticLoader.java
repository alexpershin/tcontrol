package org.tcontrol.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.Logging;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.models.SensorValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.csv.CSVParser;

import static org.tcontrol.service.impl.CSVOptions.*;

@Component
@Slf4j
@Logging
public class CSVStatisticLoader implements IStatisticsLoader {

    @Value("${dataFile}")
    private String dataFileName;

    @Setter
    @Value("${main-config.sensorValuesBufferSize:10}")
    private int capacity;

    @Override
    public List<SensorValue> load() throws FileNotFoundException {
        List<SensorValue> result = new ArrayList<>(capacity);

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader.Builder().setFile(dataFile).get()) {

            CSVFormat csvFormat = CSVFormat.EXCEL.withHeader(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);
            String csvLine;
            do {
                csvLine = reader.readLine();
                if (StringUtils.isNotBlank(csvLine)) {
                    SensorValue value = new SensorValue();
                    try (StringReader stringReader = new StringReader(csvLine)) {
                        CSVParser parser = new CSVParser(stringReader, csvFormat);
                        CSVRecord record = parser.iterator().next();

                        value.setSensorId(Integer.parseInt(record.get(SENSOR_ID)));
                        value.setSensorCode(record.get(SENSOR_CODE));
                        value.setTimestamp(Long.parseLong(record.get(TIMESTAMP)));
                        value.setValue(Double.parseDouble(record.get(VALUE)));
                        result.add(value);
                    }
                }
            } while (StringUtils.isNotBlank(csvLine) && result.size() < capacity);

            log.info("Loaded {} records", result.size());

        } catch (IOException ex) {
            log.error("error while loading CSV", ex);
        }
        return result;
    }
}
