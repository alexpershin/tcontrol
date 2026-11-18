package org.tcontrol;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.tcontrol.models.SensorValueShort;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.tcontrol.service.impl.CSVOptions.*;

public class TcontrolDataConverterRenameAlerts {

    static SimpleDateFormat OLD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final int CHUNK_SIZE = 1024 * 1024;


    public static void main(String[] args) throws Exception {
        String dataFileName = "data-last.csv";
        String resultFileName = "data_converted.csv";

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        boolean fileResultExist = Files.exists(Paths.get(resultFileName));
        if (!fileExist) {
            System.out.println("no such file: " + dataFileName);
        }
        File dataFile = new File(dataFileName);

        long t1 = System.currentTimeMillis();


        try (FileInputStream is = new FileInputStream(dataFile);
             CSVPrinter printer = new CSVPrinter(new FileWriter(resultFileName, true), CSVFormat.EXCEL)
        ) {

            if (!fileResultExist) {
                printer.printRecord(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);
            }

            String prevLine = "";
            byte[] chunk = new byte[CHUNK_SIZE];
            int chunkLen;
            int errors = 0, lines = 0, chunks = 0, alertLines=0, verandaRelayLines=0;

            SensorValueShort prev = null;
            //read file chunk by chunk
            chunk_circle:
            while ((chunkLen = is.read(chunk)) != -1) {
                ByteArrayInputStream bais = new ByteArrayInputStream(chunk);
                InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String line, lineToParse;
                int j = 0;
                long l = 0;
                //parse data line by line
                while ((line = br.readLine()) != null
                        && l < chunkLen //cut buffer according to last chunk
                ) {
                    if ((l += line.length() + 2) < chunkLen) {//not last line //add length new line symbols
                        if (j++ == 0) {
                            lineToParse = prevLine + line; //add previous chunk remains
                        } else {
                            lineToParse = line;
                        }
                        try {
                            SensorValueShort sensorValue = parseLine(lineToParse);
                            if (line.startsWith("11,102,")) {
                                if (prev == null) {
                                    printer.printRecord("102", "102", sensorValue.getTimestamp(), sensorValue.getValue());
                                    prev = sensorValue;
                                    verandaRelayLines++;
                                } else {
                                    printer.printRecord("11", "126", sensorValue.getTimestamp(), sensorValue.getValue());
                                    prev = null;
                                    alertLines++;
                                }
                            } else {
                                printer.printRecord(sensorValue.getSensorId(), sensorValue.getSensorCode(), sensorValue.getTimestamp(), sensorValue.getValue());
                            }
                            lines++;
                        } catch (RuntimeException e) {
                            errors++;
                        }
                    }
                    prevLine = line;
                }
                //clear chunk
                byte b = 0;
                Arrays.fill(chunk, b);
                chunks++;
            }

            System.out.println("lines: " + lines);
            System.out.println("varanda-lines: " + verandaRelayLines);
            System.out.println("alerts-lines: " + alertLines);
            System.out.println("errors: " + errors);
            System.out.println("chunks: " + chunks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("time: " + (t2 - t1));

    }

    private static SensorValueShort parseLine(String line) {
        if (StringUtils.isNotBlank(line)) {
            String[] cells = StringUtils.split(line, ",");
            try {
                int sensorId = Integer.parseInt(cells[0]);
                String sensorCode = cells[1];
                long timestamp = Long.parseLong(cells[2]);
                double value = Double.parseDouble(cells[3]);
                return new SensorValueShort(sensorId, sensorCode, timestamp, value);
            } catch (Exception ex) {//in case of bad file system
                System.err.println("Error while loading CSV line: '" + line + "' " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        } else {
            throw new RuntimeException("line is null");
        }
    }

}