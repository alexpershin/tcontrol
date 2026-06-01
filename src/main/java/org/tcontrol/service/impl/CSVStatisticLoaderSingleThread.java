package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.tcontrol.config.Logging;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.ITimer;
import org.tcontrol.service.SystemAlertsAggregator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Logging
@RequiredArgsConstructor
public class CSVStatisticLoaderSingleThread implements IStatisticsLoader {

    public static final int CHUNK_SIZE = 1024 * 1024;
    private final ITimer timer;

    private final SystemAlertsAggregator systemAlertsAggregator;

    @Value("${dataFile}")
    private String dataFileName;

    @Setter
    @Value("${main-config.sensorValuesBufferExpiredMillis:36000000}")
    private int expiredMillis;

    @Override
    public List<SensorValueShort> loadLast() throws FileNotFoundException {
        long from = timer.getCurrentTimeMillis() - expiredMillis;
        long to = timer.getCurrentTimeMillis();
        return load(from, to, null);
    }

    @Override
    public List<SensorValueShort> load(long from, long to, Integer sensorId) throws FileNotFoundException {
        long t1 = System.currentTimeMillis();

        List<SensorValueShort> r =  loadSingleThread(from, to, sensorId);

        long t2 = System.currentTimeMillis();
        log.info("load: {} mc", t2 - t1);
        return r;
    }

    private List<SensorValueShort> loadSingleThread(long from, long to, Integer sensorId) throws FileNotFoundException {
        List<SensorValueShort> result = new ArrayList<>(10000);

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        try(FileInputStream is = new FileInputStream(dataFile)) {

            //find position where required data located
            Long pos = CSVStatisticLoaderHelper.lookForPositionBeforeFrom(from, dataFile, systemAlertsAggregator);

            if(pos == null){
                return new ArrayList<>();
            }
            //skip where exactly no required data
            is.skip(pos);
            Pair<Integer, Long> exceptionsAndTimestamp = Pair.of(0, 0L);
            String prevLine = "";

            byte[] chunk = new byte[CHUNK_SIZE];
            int chunkLen;

            //read file chunk by chunk
            chunk_circle:
            while ((chunkLen = is.read(chunk)) != -1) {
                ByteArrayInputStream bais = new ByteArrayInputStream(chunk);
                long l = 0;
                InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String line, lineToParse;
                int j = 0;
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
                        exceptionsAndTimestamp = CSVStatisticLoaderHelper.parseLine(from, to, sensorId, lineToParse,
                                result, exceptionsAndTimestamp, false, 100);
                        if (exceptionsAndTimestamp == null) {
                            log.error("stopped(TOO MANY ERRORS)");
                            break chunk_circle;//if too many errors
                        }
                        if (exceptionsAndTimestamp.getValue() > to) {
                            log.info("reached TO timestamp");
                            break chunk_circle;//limit for "TO"
                        }
                    }
                    prevLine = line;
                }
                //clear chunk
                byte b = 0;
                Arrays.fill(chunk, b);
            }

            log.info("Loaded {} records", result.size());
        } catch (IOException ex) {
            log.error("Error while loading CSV", ex);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }

        return result;
    }

}
