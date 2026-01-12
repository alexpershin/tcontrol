package org.tcontrol.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.tcontrol.config.Logging;
import org.tcontrol.config.props.SensorConfig;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.ITimer;
import org.tcontrol.models.SensorValue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.csv.CSVParser;

import javax.annotation.PostConstruct;

import static org.tcontrol.service.impl.CSVOptions.*;


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
        List<SensorValue> result = new ArrayList<>(sensorConfig.getSensors().size() * 60 * 24);

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        log.info("Loading started");
        try (RandomAccessFile reader = new RandomAccessFile(dataFile.getPath(), "r")) {
            try {
                long pos = findPositionBeforeFrom(from, dataFile);
                reader.seek(pos + 2);
                SensorValue value;
                int exceptionCount = 0;
                long timestamp = 0;
                String csvLine;
                do {
                    csvLine = reader.readLine();
                    value = null;
                    if (StringUtils.isNotBlank(csvLine)) {
                        try (StringReader stringReader = new StringReader(csvLine)) {
                            CSVParser parser = new CSVParser(stringReader, csvFormat);
                            CSVRecord record = parser.iterator().next();
                            String valueTimestampString = record.get(TIMESTAMP);
                            Integer recordSensorId = Integer.parseInt(record.get(SENSOR_ID));
                            timestamp = Long.parseLong(valueTimestampString);
                            if ((sensorId == null || sensorId.equals(recordSensorId))
                                    && timestamp >= from
                                    && timestamp <= to) {
                                value = new SensorValue();
                                value.setSensorId(sensorId);
                                value.setSensorCode(record.get(SENSOR_CODE));
                                value.setTimestamp(timestamp);
                                value.setValue(Double.parseDouble(record.get(VALUE)));
                                result.add(value);
                            }
                        } catch (Exception ex) {//in case of bad file system
                            log.error("Error while loading CSV line: {}", csvLine, ex);
                            if (++exceptionCount >= 100) {
                                break;
                            }
                        }
                    }
                } while (csvLine != null && timestamp <= to);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }

//            SensorValue value;
//            int exceptionCount = 0;
//            do {
//                String csvLine = reader.readLine();
//                value = null;
//                if (StringUtils.isNotBlank(csvLine)) {
//                    try (StringReader stringReader = new StringReader(csvLine)) {
//                        CSVParser parser = new CSVParser(stringReader, csvFormat);
//                        CSVRecord record = parser.iterator().next();
//                        String valueTimestampString = record.get(TIMESTAMP);
//                        long timestamp = Long.parseLong(valueTimestampString);
//                        if (timestamp <= to) {
//                            value = new SensorValue();
//                            value.setSensorId(Integer.parseInt(record.get(SENSOR_ID)));
//                            value.setSensorCode(record.get(SENSOR_CODE));
//                            value.setTimestamp(Long.parseLong(valueTimestampString));
//                            value.setValue(Double.parseDouble(record.get(VALUE)));
//                            result.add(value);
//                        }
//                    } catch (Exception ex) {//in case of bad file system
//                        log.error("Error while loading CSV line: {}", csvLine, ex);
//                        if (++exceptionCount >= 100) {
//                            break;
//                        }
//                    }
//                }
//            } while (value == null || value.getTimestamp() >= from);

            log.info("Loaded {} records", result.size());

        } catch (IOException ex) {
            log.error("Error while loading CSV", ex);
        }
        return result;
    }

    private long findPositionBeforeFrom(long from, File dataFile) throws FileNotFoundException {
        try {
            Pair<Integer, Long> firstData = calculateAvgLineLengthAndFirstDataTimestamp(dataFile);
            long beginTimestamp = firstData.getValue();
            if (from < beginTimestamp) {
                throw new IllegalArgumentException("No data");
            }
            Long lastTimeStamp = readLastDataLineTimestamp(dataFile);
            if (from > lastTimeStamp) {
                throw new IllegalArgumentException("No data");
            }
            long timestampDelta = from - beginTimestamp;
            long filePeriod = lastTimeStamp - beginTimestamp;
            Path path = Paths.get(dataFile.getPath());
            long bytes = Files.size(path);
            long pos = Math.round((double) timestampDelta / filePeriod * bytes);
            Long timestampOnPos = null, timestampOnPosPrev;
            Long prevPosDelta, newPosDelta = null;
            Pair<Long, Long> timestampOnPosResult;
            do {
                timestampOnPosPrev = timestampOnPos;
                prevPosDelta = newPosDelta;
                timestampOnPosResult = timestampOnPos(pos, path);
                timestampOnPos = timestampOnPosResult.getKey();
                long newPos = timestampOnPosResult.getValue();
                long curDelta = timestampOnPos - from;
                long posDelta = Math.round(((double) curDelta / 2) / filePeriod * bytes);
                pos -= posDelta;
                newPosDelta = newPos - pos;
            } while (timestampOnPosPrev == null ||
                    timestampOnPos <= from && timestampOnPosPrev <= from
                            && (prevPosDelta == null || Math.abs(prevPosDelta) > Math.abs(newPosDelta))
                            && Math.abs(newPosDelta) > 100);
            return timestampOnPos < from ?
                    pos :
                    timestampOnPos(pos - newPosDelta, path).getValue();
        } catch (IOException e) {
            log.error("Error while loading CSV ", e);
        }
        return 0;
    }

    private Pair<Integer, Long> calculateAvgLineLengthAndFirstDataTimestamp(File dataFile) throws IOException {
        String csvLine = "";
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
                        Long timestamp = Long.parseLong(valueTimestampString);
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

    /**
     * @param posArg
     * @param path
     * @return timestamp and pos;
     * @throws IOException
     */
    Pair<Long, Long> timestampOnPos(long posArg, Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {

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
    }

}
