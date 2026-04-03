package org.tcontrol;

import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.impl.CSVStatisticSaver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TcontrolDataConverterFromLog {
    static String regex =
            "(?<time>\\d{2}:\\d{2}:\\d{2}\\.\\d{3})" +
                    "\\s(\\d{9})\\s" +
                    "(.*):\\s" +
                    "(?<id>\\d{1}),\\s" +
                    "(?<code>.*),\\s" +
                    "(?<timestamp>\\d*),\\s" +
                    "(?<value>.+)";

    public static void main(String[] args) {
        String folder = "logs-to-convert";
        String dataFileName = "logs-to-convert/log-2026-02-27.log";
        String resultFileName = "data_log_converted.csv";
        final Pattern pattern = Pattern.compile(regex);
        try (Stream<Path> list = Files.list(Path.of(folder))) {
            int n = list.map(f -> convert(resultFileName, f.toString(), pattern)).reduce(Integer::sum).orElse(0);
            System.out.println("total: " + n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static int convert(String resultFileName, String dataFileName, Pattern pattern) {
        System.out.println("converting: " + dataFileName);
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
            ArrayList<SensorValueShort> result = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (l != 0 && !line.startsWith("\0\0")) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        // System.out.println("time: " + matcher.group("time"));

                        String sensorIdStr = matcher.group("id");
                        String codeStr = matcher.group("code");
                        String timestampStr = matcher.group("timestamp");
                        String valueStr = matcher.group("value");

//                        System.out.println("id: " + sensorIdStr);
//                        System.out.println("codeStr: " + codeStr);
//                        System.out.println("timestampStr: " + timestampStr);
//                        System.out.println("value: " + valueStr);

                        int sensorId = Integer.parseInt(sensorIdStr);
                        long timestamp = Long.parseLong(timestampStr);
                        double value = Double.parseDouble(valueStr);
                        result.add(new SensorValueShort(sensorId, codeStr, timestamp, value));
                    }
                }
                l++;

            }
            csvStatisticSaver.write(result);
            int size = result.size();
            System.out.println("loaded: " + size);
            return size;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
