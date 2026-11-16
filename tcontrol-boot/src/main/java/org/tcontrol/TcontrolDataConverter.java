package org.tcontrol;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.impl.CSVStatisticSaver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.tcontrol.service.impl.CSVOptions.*;
import static org.tcontrol.service.impl.CSVStatisticLoaderHelper.*;

public class TcontrolDataConverter {

    public static void main(String[] args) {
        String dataFileName = "data-old-2.csv";
        String resultFileName = "data_converted.csv";

        CSVStatisticSaver csvStatisticSaver = new CSVStatisticSaver();
        csvStatisticSaver.setDataFileName(resultFileName);
        boolean fileExist = Files.exists(Paths.get(dataFileName));

        if (!fileExist) {
            System.out.println("no such file: " + dataFileName);
        }
        File dataFile = new File(dataFileName);

        try (FileInputStream is = new FileInputStream(dataFile)) {
            long l = 0;
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (l != 0 && !line.startsWith("\0\0")) {
                    ArrayList<SensorValueShort> result = new ArrayList<>();
                    String[] cells = StringUtils.split(line, ",");
                    if (cells.length == 3) {
                        if(line.startsWith("0,2") && (line.endsWith(",0.0") || line.endsWith(",1.0"))){//skip
                            System.out.println("skiped: " + line);
                            continue;
                        }
                        result.add(parseOldLine(line));
                    } else {
                        result.add(parseNewLine(cells));
                    }
                    csvStatisticSaver.write(result);
                }
                l++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static CSVFormat CSV_FORMAT_OLD = CSVFormat.EXCEL.withHeader(SENSOR_ID, TIMESTAMP, VALUE);
    static SimpleDateFormat OLD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static SensorValueShort parseOldLine(String line) {

        if (StringUtils.isNotBlank(line)) {
            try (StringReader stringReader = new StringReader(line)) {
                CSVParser parser = new CSVParser(stringReader, CSV_FORMAT_OLD);
                CSVRecord record = parser.iterator().next();
                String valueTimestampString = record.get(TIMESTAMP);
                Date date = OLD_DATE_FORMAT.parse(valueTimestampString);
                String sensorIdStr = record.get(SENSOR_ID);
                int sensorId = Integer.parseInt(sensorIdStr);
                double value = Double.parseDouble(record.get(VALUE));
                return new SensorValueShort(sensorId, null, date.getTime(), value);
            } catch (Exception ex) {//in case of bad file system
                System.err.println("Error while loading CSV line: '" + line + "' " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        } else {
            throw new RuntimeException("line is null");
        }

    }

    private static SensorValueShort parseNewLine(String[] cells) {
        int sensorId = Integer.parseInt(cells[SENSOR_ID_INDX]);
        String sensorCode = cells[SENSOR_CODE_INDX];
        double value = Double.parseDouble(cells[SENSOR_VALUE_INDX]);
        String timestampStr = cells[TIMESTAMP_INDX];
        long timestamp;
        try {
            Date date = OLD_DATE_FORMAT.parse(timestampStr);
            timestamp = date.getTime();
        } catch (ParseException e) {
            timestamp = Long.parseLong(timestampStr);
        }
        return new SensorValueShort(sensorId, sensorCode, timestamp, value);
    }

}
