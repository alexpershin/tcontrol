package org.tcontrol.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.SystemAlertsAggregator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.tcontrol.service.impl.CSVOptions.*;

@Slf4j
public class CSVStatisticLoaderHelper {
    public static final int SENSOR_ID_INDX = 0;
    public static final int TIMESTAMP_INDX = 2;
    public static final int SENSOR_CODE_INDX = 1;
    public static final int SENSOR_VALUE_INDX = 3;
    public static final String RESULT_POSITION_BEFORE_FROM = "lookForPositionBeforeFrom(): result > from";

    public static CSVFormat CSV_FORMAT = CSVFormat.EXCEL.withHeader(SENSOR_ID, SENSOR_CODE, TIMESTAMP, VALUE);

    public static Pair<Integer, Long> parseLine(long from, long to, Integer sensorId, String csvLine, List<SensorValueShort> result, Pair<Integer, Long> exceptionsAndTimestamp, boolean first, int errors) {
        return parseLine(from, to, sensorId, csvLine, result, exceptionsAndTimestamp, first, errors, false);
    }

    public static Pair<Integer, Long> parseLine(long from, long to, Integer sensorId, String csvLine, List<SensorValueShort> result, Pair<Integer, Long> exceptionsAndTimestamp, boolean first, int errors, boolean printError) {
        if (StringUtils.isNotBlank(csvLine)) {
            try {
                if (csvLine.equals("1,,1753538821893,22.187")) {
                    System.out.println("c");
                }
                String[] cells = csvLine.split(",", -1);
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
                    if (printError) {
                        log.error("Error while loading CSV line: {}", csvLine, ex);
                    }
                    exceptionsAndTimestamp = Pair.of(exceptionsAndTimestamp.getKey() + 1, exceptionsAndTimestamp.getValue());
                    if (exceptionsAndTimestamp.getKey() >= errors) {
                        return null;
                    }
                }
            }
        }
        return exceptionsAndTimestamp;
    }

    static Long lookForPositionBeforeFrom(long from, File dataFile, SystemAlertsAggregator systemAlertsAggregator) throws FileNotFoundException {
        log.info("looking for good position in data file from {}..", new Date(from));
        try {
            final Pair<Double, Long> firstData = calculateAvgLineLengthAndFirstDataTimestamp(dataFile);
            long beginTimestamp = firstData.getValue();
            //Double avgLineLength = firstData.getKey();
            if (from < beginTimestamp) {
                log.info("no data");
                return 0L;
            }
            final Long lastTimeStamp = readLastDataLineTimestamp(dataFile);
            if (from > lastTimeStamp) {
                log.info("no data");
                return null;//no data
            }
            long timestampDelta = from - beginTimestamp;
            long filePeriod = lastTimeStamp - beginTimestamp;
            Path path = Paths.get(dataFile.getPath());
            long bytes = Files.size(path);
            long pos = Math.round((double) timestampDelta / filePeriod * bytes);

            final Pair<Long, Long> timestampOnPosResult = timestampOnPos(pos, path);
            Long timestampOnPos = timestampOnPosResult.getKey();
            long newPos = timestampOnPosResult.getValue();
            long pos1, pos2;
            if (timestampOnPos > from && !timestampOnPos.equals(lastTimeStamp)) {
                pos1 = 0;
                pos2 = newPos;
            } else if (timestampOnPos.equals(lastTimeStamp)) {
                pos1 = 0;
                pos2 = newPos;
            } else {
                pos1 = newPos;
                pos2 = bytes;
            }
            Long res = 0L;
            long prevPos = 0L;

            int iterations = 0;
            Long timestampResult = null;
            if (pos1 >= pos2) {
                res = null;//no data
            } else {
                do {
                    long posDelta = pos2 - pos1;
                    long middlePos = pos1 + posDelta / 2;
                    final Pair<Long, Long> timestampOnMiddleResult = timestampOnPos(middlePos, path);
                    Long timestampMiddle = timestampOnMiddleResult.getKey();
                    long delta = Math.abs(timestampMiddle - from);
                    if (delta <= 3600 * 1000 * 2 || posDelta < 1000) {
                        if (timestampMiddle > from) {
                            res = pos1;
                        } else {
                            res = prevPos;
                        }
                        break;
                    }
                    Long middlePosRes = timestampOnMiddleResult.getValue();
                    if (timestampMiddle > from) {
                        pos2 = middlePosRes;
                        timestampResult = timestampMiddle;
                    } else if (timestampMiddle < from) {
                        pos1 = middlePosRes;
                        timestampResult = timestampMiddle;
                    } else {
                        res = pos1;
                        break;
                    }
                    prevPos = pos1;
                } while (iterations++ < 100);
            }
            log.info("looking for position complete: pos {}, iterations: {}, pos date: {}", res, iterations,
                    timestampResult != null ? new Date(timestampOnPos(res, path).getKey()).toString() : "s");
            if (timestampResult != null && from > timestampResult) {
                if (systemAlertsAggregator != null) {
                    systemAlertsAggregator.addAlert(RESULT_POSITION_BEFORE_FROM);
                }
                log.error(RESULT_POSITION_BEFORE_FROM);
            }
            return res;
        } catch (IOException e) {
            log.error("Error while loading CSV ", e);
        }
        log.info("zero data");
        return 0L;
    }

    private static Pair<Double, Long> calculateAvgLineLengthAndFirstDataTimestamp(File dataFile) throws IOException {
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
            return Pair.of((double) sum / count, firstDataTimestamp);
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

    /**
     * @param filePath
     * @param position
     * @return <position,line>
     * @throws IOException
     */
    private static Pair<Long, String> getLineFromFile(String filePath, long position) throws IOException {
        String line;
        byte[] symbol = new byte[1];
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {

            do {
                file.seek(position);
                file.read(symbol, 0, 1);
            } while (symbol[0] != 0 && symbol[0] != '\n' && --position > 0);
            line = file.readLine();
        }
        return Pair.of(position, line);
    }
}
