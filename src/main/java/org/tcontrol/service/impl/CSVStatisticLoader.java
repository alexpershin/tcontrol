package org.tcontrol.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.ITimer;
import org.tcontrol.models.SensorValue;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.csv.CSVParser;

import javax.annotation.PostConstruct;

import static org.tcontrol.service.impl.CSVOptions.*;

@Slf4j
@Logging
@RequiredArgsConstructor
public class CSVStatisticLoader implements IStatisticsLoader {

    public static final int SENSOR_ID_INDX = 0;
    public static final int TIMESTAMP_INDX = 2;
    public static final int SENSOR_CODE_INDX = 1;
    public static final int SENSOR_VALUE_INDX = 3;
    public static final int NUMBER_CORES = Runtime.getRuntime().availableProcessors();
    private final SensorConfig sensorConfig;
    private final ITimer timer;

    @Value("${dataFile}")
    private String dataFileName;

    @Setter
    @Value("${main-config.sensorValuesBufferExpiredMillis:36000000}")
    private int expiredMillis;

    private CSVFormat csvFormat;

    @PostConstruct
    public void postConstruct() {
        csvFormat = CSVFormat.EXCEL.withHeader(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);
    }

    @Override
    public List<SensorValue> load() throws FileNotFoundException {
        return load(timer.getCurrentTimeMillis() - expiredMillis, timer.getCurrentTimeMillis(), null);
    }

    @Override
    public List<SensorValue> load(long from, long to, Integer sensorId) throws FileNotFoundException {
        //return loadSingleThread(from, to, sensorId);
        return loadMultiThread(from, to, sensorId);
    }

    public List<SensorValue> loadSingleThread(long from, long to, Integer sensorId) throws FileNotFoundException {
        List<SensorValue> result = new ArrayList<>(10000);

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        try {
            FileInputStream is = new FileInputStream(dataFile);
            //find position where required data located
            long pos = lookForPositionBeforeFrom(from, dataFile);

            //skip where exactly no required data
            is.skip(pos);
            Pair<Integer, Long> exceptionsAndTimestamp = Pair.of(0, 0L);
            String lastLine = "";

            byte[] chunk = new byte[1024 * 1024];
            int chunkLen;

            //read file chunk by chunk
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
                    if (j++ == 0) {
                        lineToParse = lastLine + line; //add previous chunk remains
                    } else {
                        lineToParse = line;
                    }
                    exceptionsAndTimestamp = parseLine(from, to, sensorId, lineToParse, result, exceptionsAndTimestamp);
                    if (exceptionsAndTimestamp == null) break;//if too many errors
                    if (exceptionsAndTimestamp.getValue() > to) break;//limit for "TO"
                    lastLine = line;
                    l += line.length() + 2;//add length new line symbols
                }
                chunk = new byte[1024 * 1024];
            }

            log.info("Loaded {} records", result.size());
        } catch (IOException ex) {
            log.error("Error while loading CSV", ex);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }

        return result;
    }

    public List<SensorValue> loadMultiThread(long from, long to, Integer sensorId) throws FileNotFoundException {

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        try {
            FileInputStream is = new FileInputStream(dataFile);
            //find position where required data located
            long pos = lookForPositionBeforeFrom(from, dataFile);

            //skip where exactly no required data
            is.skip(pos);

            byte[] chunk = new byte[1024 * 1024];
            int chunkLen;

            int threadNumber = 1 + NUMBER_CORES / 2;
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber);
            List<CSVParsingTask> taskList = new ArrayList<>();
            int taskId = 0;

            //read file chunk by chunk
            while ((chunkLen = is.read(chunk)) != -1) {

                CSVParsingTask task = new CSVParsingTask(chunk, chunkLen, from, to, sensorId, taskId++);
                taskList.add(task);

                while (executor.getActiveCount() >= threadNumber) {
                    Thread.sleep(1);
                }
                // create threads for each task
                executor.submit(task);

                chunk = new byte[1024 * 1024];
            }

            executor.shutdown();
            boolean r = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!r) {
                log.error("tasks still in progress, active count: {}", executor.getActiveCount());
            }

            List<SensorValue> commonResult =
                    taskList.stream().flatMap(t -> t.getResult().stream()).collect(Collectors.toList());

            log.info("Loaded {} records", commonResult.size());
            return commonResult;
        } catch (IOException ex) {
            log.error("Error while loading CSV", ex);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }

    @Getter
    @RequiredArgsConstructor
    public static class CSVParsingTask implements Callable<Pair<Integer, Long>> {
        private final byte[] chunk;
        private final int chunkLen;
        private final long from, to;
        private final Integer sensorId;
        private final int taskId;
        private boolean finished = false;
        private final List<SensorValue> result = new ArrayList<>();
        String firstLine, lastLine;
        private Pair<Integer, Long> exceptionsAndTimestamp = Pair.of(0, 0L);

        @Override
        public Pair<Integer, Long> call() {
            try {

                ByteArrayInputStream bais = new ByteArrayInputStream(chunk);
                long l = 0;
                InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);
                String line, prevLine = null;
                int j = 0;
                //parse data line by line
                while ((line = br.readLine()) != null
                        && l < chunkLen //cut buffer according to last chunk
                ) {
                    if (j++ == 0) {
                        firstLine = line;
                    } else {
                        if (prevLine != null) {
                            exceptionsAndTimestamp = parseLine(from, to, sensorId, prevLine, result, exceptionsAndTimestamp);
                            if (exceptionsAndTimestamp == null) break;//if too many errors
                            if (exceptionsAndTimestamp.getValue() > to) break;//limit for "TO"
                        }
                        prevLine = line;
                    }
                    l += line.length() + 2;
                }
                lastLine = line;
            } catch (IOException ex) {
                log.error("Error while loading CSV", ex);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
            finished = true;
            return exceptionsAndTimestamp;
        }
    }

    private static Pair<Integer, Long> parseLine(long from, long to,
                                                 Integer sensorId,
                                                 String csvLine,
                                                 List<SensorValue> result,
                                                 Pair<Integer, Long> exceptionsAndTimestamp) {
        if (StringUtils.isNotBlank(csvLine)) {
            try {
                String[] cells = StringUtils.split(csvLine, ",");
                int recordSensorId = Integer.parseInt(cells[SENSOR_ID_INDX]);
                if (sensorId == null || sensorId.equals(recordSensorId)) {
                    long timestamp = Long.parseLong(cells[TIMESTAMP_INDX]);
                    if (timestamp >= from) {
                        if (timestamp <= to) {
                            final SensorValue value = new SensorValue();
                            value.setSensorId(recordSensorId);
                            value.setSensorCode(cells[SENSOR_CODE_INDX]);
                            value.setTimestamp(timestamp);
                            value.setValue(Double.parseDouble(cells[SENSOR_VALUE_INDX]));
                            result.add(value);
                        }
                        exceptionsAndTimestamp = Pair.of(exceptionsAndTimestamp.getKey(), timestamp);
                    }
                }
            } catch (Exception ex) {//in case of bad file system
                log.error("Error while loading CSV line: {}", csvLine, ex);
                exceptionsAndTimestamp = Pair.of(exceptionsAndTimestamp.getKey() + 1, exceptionsAndTimestamp.getValue());
                if (exceptionsAndTimestamp.getKey() >= 100) {
                    return null;
                }
            }
        }
        return exceptionsAndTimestamp;
    }

    private long lookForPositionBeforeFrom(long from, File dataFile) throws FileNotFoundException {
        log.info("looking for good position in data file..");
        try {
            final Pair<Integer, Long> firstData = calculateAvgLineLengthAndFirstDataTimestamp(dataFile);
            long beginTimestamp = firstData.getValue();
            if (from < beginTimestamp) {
                throw new IllegalArgumentException("No data");
            }
            final Long lastTimeStamp = readLastDataLineTimestamp(dataFile);
            if (from > lastTimeStamp) {
                throw new IllegalArgumentException("No data");
            }
            long timestampDelta = from - beginTimestamp;
            long filePeriod = lastTimeStamp - beginTimestamp;
            Path path = Paths.get(dataFile.getPath());
            long bytes = Files.size(path);
            long pos = Math.round((double) timestampDelta / filePeriod * bytes);
            Long timestampOnPos = null, timestampOnPosPrev;
            long newPos;
            Long prevPosDelta, newPosDelta = null;
            int iterations = 0;
            do {
                timestampOnPosPrev = timestampOnPos;
                prevPosDelta = newPosDelta;
                final Pair<Long, Long> timestampOnPosResult = timestampOnPos(pos, path);
                timestampOnPos = timestampOnPosResult.getKey();
                newPos = timestampOnPosResult.getValue();
                final long curDelta = timestampOnPos - from;
                final long posDelta = Math.round((double) curDelta / filePeriod * bytes);
                pos -= posDelta;
                newPosDelta = newPos - pos;
                iterations++;
            } while (timestampOnPosPrev == null
                    || timestampOnPos <= from && timestampOnPosPrev <= from
                    && Math.abs(prevPosDelta) > Math.abs(newPosDelta)
                    && Math.abs(newPosDelta) > 100);
            long res = timestampOnPos < from ?
                    newPos + 1 :
                    timestampOnPos(pos - newPosDelta, path).getValue() + 1;
            log.info("looking for position complete: pos {}, iterations: {}", res, iterations);
            return res;
        } catch (IOException e) {
            log.error("Error while loading CSV ", e);
        }
        return 0;
    }

    private Pair<Integer, Long> calculateAvgLineLengthAndFirstDataTimestamp(File dataFile) throws IOException {
        String csvLine;
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(dataFile, "r")) {
            csvLine = randomAccessFile.readLine();//read header
            Long firstDataTimestamp = null;
            int count = 0;
            int sum = 0;
            int exceptionCount = 0;
            while (csvLine != null && count < 10) {
                csvLine = randomAccessFile.readLine();
                if (StringUtils.isNotBlank(csvLine)) {
                    try (StringReader stringReader = new StringReader(csvLine)) {
                        CSVParser parser = new CSVParser(stringReader, csvFormat);
                        CSVRecord record = parser.iterator().next();
                        String valueTimestampString = record.get(TIMESTAMP);
                        long timestamp = Long.parseLong(valueTimestampString);
                        if (firstDataTimestamp == null) {
                            firstDataTimestamp = timestamp;
                        }
                        count++;
                        sum += csvLine.length();
                    } catch (Exception ex) {//in case of bad file system
                        log.error("Error while loading CSV line: {}", csvLine, ex);
                        if (++exceptionCount >= 100) {
                            break;
                        }
                    }
                }
            }
            return Pair.of(sum / count, firstDataTimestamp);
        }
    }

    private Long readLastDataLineTimestamp(File dataFile) {
        Long timestamp = null;
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader.Builder().setFile(dataFile).get()) {
            int exceptionCount = 0;
            String csvLine = null;
            do {
                csvLine = reader.readLine();
                if (StringUtils.isNotBlank(csvLine)) {
                    try (StringReader stringReader = new StringReader(csvLine)) {
                        CSVParser parser = new CSVParser(stringReader, csvFormat);
                        CSVRecord record = parser.iterator().next();
                        String valueTimestampString = record.get(TIMESTAMP);
                        timestamp = Long.parseLong(valueTimestampString);
                    } catch (Exception ex) {//in case of bad file system
                        log.error("Error while loading CSV line: {}", csvLine, ex);
                        if (++exceptionCount >= 100) {
                            break;
                        }
                    }
                }
            } while (timestamp == null && csvLine != null);

        } catch (IOException ex) {
            log.error("Error while loading CSV", ex);
        }
        return timestamp;
    }

    /**
     * @param posArg
     * @param path
     * @return timestamp and pos;
     * @throws IOException
     */
    Pair<Long, Long> timestampOnPos(long posArg, Path path) throws IOException {
        int exceptionCount = 0;
        Long timestamp = 0L;
        Pair<Long, String> r;
        String currentLine;
        long pos = posArg;
        do {
            r = getLineFromFile(path.toString(), pos);
            currentLine = r.getValue();
            try (StringReader stringReader = new StringReader(currentLine)) {
                CSVParser parser = new CSVParser(stringReader, csvFormat);
                CSVRecord record = parser.iterator().next();
                String valueTimestampString = record.get(TIMESTAMP);
                timestamp = Long.parseLong(valueTimestampString);
            } catch (Exception ex) {//in case of bad file system
                log.error("Error while loading CSV line: {}", currentLine, ex);
                if (++exceptionCount >= 100) {
                    break;
                } else {
                    pos += r.getKey();
                }
            }
        } while (timestamp == null && currentLine != null);
        return Pair.of(timestamp, r.getKey());
    }


    public static Pair<Long, String> getLineFromFile(String filePath, long position) throws IOException {
        String line;
        byte[] symbol = new byte[1];
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            do {
                file.seek(position);
                file.read(symbol, 0, 1);
            } while (symbol[0] != '\n' && --position > 0);
            line = file.readLine();
        }
        return Pair.of(position, line);
    }

}
