package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.ITimer;
import org.tcontrol.models.SensorValue;

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
@RequiredArgsConstructor
public class CSVStatisticLoader implements IStatisticsLoader {

    private final SensorConfig sensorConfig;
    private final ITimer timer;

    @Value("${dataFile}")
    private String dataFileName;

    @Setter
    @Value("${main-config.sensorValuesBufferExpiredMillis:36000000}")
    private int expiredMillis;

    @Override
    public List<SensorValue> load() throws FileNotFoundException {
        List<SensorValue> result = new ArrayList<>(sensorConfig.getSensors().size() * 60 * 24);

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        log.info("Loading started");
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader.Builder().setFile(dataFile).get()) {

            CSVFormat csvFormat = CSVFormat.EXCEL.withHeader(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);

            SensorValue value;
            do {
                String csvLine = reader.readLine();
                value = null;
                if (StringUtils.isNotBlank(csvLine)) {
                    value = new SensorValue();
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
            } while (value != null && timer.getCurrentTimeMillis() - value.getTimestamp() < expiredMillis);

            log.info("Loaded {} records", result.size());

        } catch (IOException ex) {
            log.error("error while loading CSV", ex);
        }
        Collections.reverse(result);
        return result;
    }
}
