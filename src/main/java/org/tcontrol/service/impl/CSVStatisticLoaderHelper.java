package org.tcontrol.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.tcontrol.models.SensorValueShort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.tcontrol.service.impl.CSVOptions.*;
import static org.tcontrol.service.impl.CSVOptions.VALUE;

@Slf4j
public class CSVStatisticLoaderHelper {
    public static final int SENSOR_ID_INDX = 0;
    public static final int TIMESTAMP_INDX = 2;
    public static final int SENSOR_CODE_INDX = 1;
    public static final int SENSOR_VALUE_INDX = 3;

    private static CSVFormat CSV_FORMAT = CSVFormat.EXCEL.withHeader(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);

    static Pair<Integer, Long> parseLine(long from, long to,
                                         Integer sensorId,
                                         String csvLine,
                                         List<SensorValueShort> result,
                                         Pair<Integer, Long> exceptionsAndTimestamp,
                                         boolean first) {
        if (StringUtils.isNotBlank(csvLine)) {
            try {
                String[] cells = StringUtils.split(csvLine, ",");
                int recordSensorId = Integer.parseInt(cells[SENSOR_ID_INDX]);
                if (sensorId == null || sensorId.equals(recordSensorId)) {
                    long timestamp = Long.parseLong(cells[TIMESTAMP_INDX]);
                    if (timestamp >= from) {
                        if (timestamp <= to) {
                            final SensorValueShort value = new SensorValueShort();
                            value.setSensorId(recordSensorId);
                            value.setTimestamp(timestamp);
                            value.setValue(Double.parseDouble(cells[SENSOR_VALUE_INDX]));
                            value.setSensorCode(cells[SENSOR_CODE_INDX]);
                            result.add(value);
                        }
                        exceptionsAndTimestamp = Pair.of(exceptionsAndTimestamp.getKey(), timestamp);
                    }
                }
            } catch (Exception ex) {//in case of bad file system
                if (!first) {
                    log.error("Error while loading CSV line: {}", csvLine, ex);
                    exceptionsAndTimestamp = Pair.of(exceptionsAndTimestamp.getKey() + 1, exceptionsAndTimestamp.getValue());
                    if (exceptionsAndTimestamp.getKey() >= 100) {
                        return null;
                    }
                }
            }
        }
        return exceptionsAndTimestamp;
    }

    static long lookForPositionBeforeFrom(long from, File dataFile) throws FileNotFoundException {
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
            long res;

            if (timestampOnPos < from) {
                res = newPos + 1;
            } else {
                Pair<Long, Long> longLongPair = timestampOnPos(pos - newPosDelta, path);
                timestampOnPos = longLongPair.getKey();
                res = longLongPair.getValue() + 1;
            }

            log.info("looking for position complete: pos {}, iterations: {}, pos date: {}", res, iterations, new Date(timestampOnPos));
            return res;
        } catch (IOException e) {
            log.error("Error while loading CSV ", e);
        }
        return 0;
    }

    private static Pair<Integer, Long> calculateAvgLineLengthAndFirstDataTimestamp(File dataFile) throws IOException {
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
                        CSVParser parser = new CSVParser(stringReader, CSV_FORMAT);
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

    private static Long readLastDataLineTimestamp(File dataFile) {
        Long timestamp = null;
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader.Builder().setFile(dataFile).get()) {
            int exceptionCount = 0;
            String csvLine = null;
            do {
                csvLine = reader.readLine();
                if (StringUtils.isNotBlank(csvLine)) {
                    try (StringReader stringReader = new StringReader(csvLine)) {
                        CSVParser parser = new CSVParser(stringReader, CSV_FORMAT);
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
    private static Pair<Long, Long> timestampOnPos(long posArg, Path path) throws IOException {
        int exceptionCount = 0;
        Long timestamp = 0L;
        Pair<Long, String> r;
        String currentLine;
        long pos = posArg;
        do {
            r = getLineFromFile(path.toString(), pos);
            currentLine = r.getValue();
            try (StringReader stringReader = new StringReader(currentLine)) {
                CSVParser parser = new CSVParser(stringReader, CSV_FORMAT);
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

   private  static Pair<Long, String> getLineFromFile(String filePath, long position) throws IOException {
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
