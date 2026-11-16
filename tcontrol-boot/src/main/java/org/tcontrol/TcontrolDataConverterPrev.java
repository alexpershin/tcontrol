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
import java.util.*;

import static org.tcontrol.service.impl.CSVOptions.*;

public class TcontrolDataConverterPrev {

    public static final String LINE_IS_NULL = "line is null";
    static SimpleDateFormat OLD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final int CHUNK_SIZE = 1024 * 1024;

    static Map<String, Pair<Integer, String>> sensors = new HashMap<>(); //old_code-> id, new_code

    static {
        //старый датчик/новый датчик
        sensors.put("28-000002c7c3ee", Pair.of(0, "28-000002c7c3ee")); //коридор
        sensors.put("28-031701a152ff", Pair.of(1, "28-031701a152ff")); //улица
        sensors.put("28-000002c7eed3", Pair.of(2, "28-000002c7eed3")); //подвал
        sensors.put("28-0000058ddc1d", Pair.of(3, "28-0000058ddc1d")); //щит
        sensors.put("28-0000066c6a65", Pair.of(3, "28-0000066c6a65"));
        sensors.put("28-0417019b15ff", Pair.of(4, "28-0417019b15ff"));//веранда
        sensors.put("28-0317017a6aff", Pair.of(5, "28-0317017a6aff"));//моя комната
        sensors.put("28-031701e06cff", Pair.of(6, "28-031701e06cff"));//горячая вода
        sensors.put("28-0517019b22ff", Pair.of(6, "28-0517019b22ff"));
        sensors.put("0", Pair.of(9, "100")); //Электрокотел
        sensors.put("1", Pair.of(10, "101")); //Газовый котел
        sensors.put("2", Pair.of(102, "102"));//веранда циркуляция
        sensors.put("3", Pair.of(103, "103"));//моя комната
        sensors.put("4", Pair.of(104, "104"));//коридор
        sensors.put("5", Pair.of(125, "125"));//питание от солнечной панели
        sensors.put("6", Pair.of(106, "106"));//гор. вода
    }


    public static void main(String[] args) throws Exception {
        String dataFileName = "data-old-1.csv";
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
            int errors = 0, lines = 0, chunks = 0;

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
                while (l <= chunkLen  //cut buffer according to last chunk
                        && (line = br.readLine()) != null

                ) {
//                    if ("28-000002c7eed3,2021-05-15 07:15:02.638,15.375".equals(line)) {
//                        System.out.println("a");
//                    }
//                    if ("28-000002c7eed3,2022-05-03 11:19:02.709,15.312".equals(line)) {
//                        System.out.println("b");
//                    }
//
//                    if(l + line.length() + 2 >= chunkLen-10){
//                        System.out.println("c");
//                    }
//

                    if ((l += line.length() + 2) <= chunkLen) {//not last line //add length new line symbols
                        if (j++ == 0) {
                            lineToParse = prevLine + line; //add previous chunk remains
                        } else {
                            lineToParse = line;
                        }
                        try {
                            SensorValueShort resultValue = parseOldLine(lineToParse);
                            printer.printRecord(resultValue.getSensorId(), resultValue.getSensorCode(), resultValue.getTimestamp(), resultValue.getValue());
                            lines++;
                        } catch (RuntimeException ex) {
                            if (ex.getMessage() != LINE_IS_NULL) {
                                errors++;
                            }
                        }
                        prevLine = "";
                    } else if (chunk[chunkLen - 1] == '\n' || chunk[chunkLen - 1] == '\r') {//full last line of the chunk
                        try {
                            SensorValueShort resultValue = parseOldLine(line);
                            printer.printRecord(resultValue.getSensorId(), resultValue.getSensorCode(), resultValue.getTimestamp(), resultValue.getValue());
                            lines++;
                        } catch (RuntimeException ex) {
                            if (ex.getMessage() != LINE_IS_NULL) {
                                errors++;
                            }
                        }
                        prevLine = "";
                    } else {
                        prevLine = line;
                    }
                }
                //clear chunk
                byte b = 0;
                Arrays.fill(chunk, b);
                chunks++;
            }

            System.out.println("lines: " + lines);
            System.out.println("errors: " + errors);
            System.out.println("chunks: " + chunks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long t2 = System.currentTimeMillis();
        System.out.println("time: " + (t2 - t1));

    }

    private static int SENSOR_CODE_IDX = 0;
    private static int TIMESTAMP_IDX = 1;
    private static int VALUE_IDX = 2;

    private static SensorValueShort parseOldLine(String initLine) {
        String line = removeNulls(initLine);

        if (StringUtils.isNotBlank(line)) {
            String[] cells = StringUtils.split(line, ",");
            try {
                String valueTimestampString = cells[TIMESTAMP_IDX];
                Date date = OLD_DATE_FORMAT.parse(valueTimestampString);
                String sensorCodeOld = cells[SENSOR_CODE_IDX];
                Pair<Integer, String> sencorIdAndNewCode = sensors.get(sensorCodeOld);
                int sensorId = sencorIdAndNewCode.getLeft();
                String sensorCodeNew = sencorIdAndNewCode.getRight();
                double value = Double.parseDouble(cells[VALUE_IDX]);
                return new SensorValueShort(sensorId, sensorCodeNew, date.getTime(), value);
            } catch (Exception ex) {//in case of bad file system
                System.err.println("Error while loading CSV line: '" + line + "' " + ex.getMessage());
                throw new RuntimeException(ex);
            }
        } else {
            throw new RuntimeException(LINE_IS_NULL);
        }
    }

    private static String removeNulls(String initLine) {
        byte[] bytes = initLine.getBytes();
        int i = 0;
        for (; i < bytes.length; i++) {
            if (bytes[i] != 0) {
                break;
            }
        }
        String line = initLine.substring(i);
        return line;
    }

}