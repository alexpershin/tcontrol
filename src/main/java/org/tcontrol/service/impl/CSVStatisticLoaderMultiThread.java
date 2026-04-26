package org.tcontrol.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.tcontrol.config.Logging;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.IStatisticsLoader;
import org.tcontrol.service.ITimer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Logging
@RequiredArgsConstructor
public class CSVStatisticLoaderMultiThread implements IStatisticsLoader {

    public static final int NUMBER_CORES = Runtime.getRuntime().availableProcessors();
    public static final int CHUNK_SIZE = 2*1024 * 1024;
    private final ITimer timer;

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

        List<SensorValueShort> r = loadMultiThread(from, to, sensorId);

        long t2 = System.currentTimeMillis();
        log.info("load: {} mc", t2 - t1);
        return r;
    }

    private List<SensorValueShort> loadMultiThread(long from, long to, Integer sensorId) throws FileNotFoundException {

        boolean fileExist = Files.exists(Paths.get(dataFileName));
        if (!fileExist) {
            throw new FileNotFoundException(dataFileName);
        }
        File dataFile = new File(dataFileName);

        try(FileInputStream is = new FileInputStream(dataFile)) {

            //find position where required data located
            Long pos = CSVStatisticLoaderHelper.lookForPositionBeforeFrom(from, dataFile);
            if(pos == null){
                return new ArrayList<>();
            }

            //skip where exactly no required data
            is.skip(pos);

            byte[] chunk = new byte[CHUNK_SIZE];
            int chunkLen;

            int threadNumber = 1 + NUMBER_CORES / 5;
            log.info("threads: {}", threadNumber);
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNumber);
            List<CSVParsingTask> taskList = new ArrayList<>();
            int taskId = 0;

            List<SensorValueShort> commonResult = new ArrayList<>();
            //read file chunk by chunk
            while ((chunkLen = is.read(chunk)) != -1) {

                CSVParsingTask task = new CSVParsingTask(chunk, chunkLen, from, to, sensorId, taskId++);
                taskList.add(task);

                while (executor.getActiveCount() >= threadNumber) {
                    Thread.sleep(1);
                }
                //Thread.sleep(1);
                log.info("task started: {}", taskList.size() - 1);
                // create threads for each task
                executor.submit(task);
                aggregate(taskList, commonResult, from, to, sensorId);

                if (taskList.stream()
                        .map(CSVParsingTask::isStopped)
                        .reduce((a, b) -> a || b)
                        .orElse(false)) {
                    log.info("stop creating new tasks: {}", taskList.size() - 1);
                    break;
                }

                chunk = new byte[CHUNK_SIZE];
            }

            executor.shutdown();
            boolean r = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!r) {
                log.error("tasks still in progress, active count: {}", executor.getActiveCount());
            }

            aggregate(taskList, commonResult, from, to, sensorId);
            processEdges(taskList, commonResult, from, to, sensorId);

            List<SensorValueShort> result = commonResult.stream()
                    .sorted((a, b) -> Comparator.comparingLong(SensorValueShort::getTimestamp).compare(a, b))
                    .collect(Collectors.toList());

            log.info("Loaded {} records", result.size());
            return result;
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
    static class CSVParsingTask implements Callable<Pair<Integer, Long>> {
        private byte[] chunk;
        private final int chunkLen;
        private final long from;
        private final long to;
        private final Integer sensorId;
        private final int taskId;

        private volatile boolean finished = false;
        private volatile boolean stopped = false;
        private List<SensorValueShort> result = new ArrayList<>();
        private String firstLine, lastLine;
        private Pair<Integer, Long> exceptionsAndTimestamp = Pair.of(0, 0L);

        public CSVParsingTask(byte[] chunk, int chunkLen, long from, long to, Integer sensorId, int taskId) {
            this.chunk = chunk;
            this.chunkLen = chunkLen;
            this.from = from;
            this.to = to;
            this.sensorId = sensorId;
            this.taskId = taskId;
        }

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
                            exceptionsAndTimestamp = CSVStatisticLoaderHelper.parseLine(from, to, sensorId, prevLine, result, exceptionsAndTimestamp, false, 100);
                            if (exceptionsAndTimestamp == null) { //if too many errors
                                log.error("task {} stopped(TOO MANY ERRORS) ", taskId);
                                stopped = true;
                            }
                            if (exceptionsAndTimestamp.getValue() > to) {//limit for "TO"
                                log.info("task {} reached TO timestamp", taskId);
                                stopped = true;
                                break;
                            }
                        }
                        prevLine = line;
                    }
                    l += line.length() + 2;
                }
                lastLine = prevLine;
            } catch (IOException ex) {
                log.error("Error while loading CSV", ex);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
            finished = true;
            log.info("task {} finished", taskId);
            return exceptionsAndTimestamp;
        }

        public void clean() {
            result.clear();
            result = null;
            chunk = null;
        }
    }


    static void aggregate(List<CSVParsingTask> taskList, List<SensorValueShort> commonResult,
                          long from, long to, Integer sensorId) {

        List<CSVParsingTask> finishedTasks = taskList.stream()
                .filter(t -> t.isFinished() || t.isStopped())
                .filter(t -> t.getResult() != null)
                .filter(t -> !t.getResult().isEmpty())
                .collect(Collectors.toList());

        commonResult.addAll(finishedTasks.stream()
                .flatMap(t -> t.getResult().stream())
                .collect(Collectors.toList()));

        finishedTasks.forEach(CSVParsingTask::clean);

        log.info("joined data for tasks: {}",
                finishedTasks.stream()
                        .map(CSVParsingTask::getTaskId)
                        .map(Objects::toString)
                        .collect(Collectors.joining(","))
        );
    }

    static void processEdges(List<CSVParsingTask> taskList, List<SensorValueShort> commonResult,
                             long from, long to,
                             Integer sensorId) {
        int sizeBefore = commonResult.size();
        CSVParsingTask prevTask = null;
        for (int i = 0; i < taskList.size(); i++) {
            CSVParsingTask curTask = taskList.get(i);

            final String lineToParse = Objects.toString(prevTask != null ? prevTask.getLastLine() : null, "")
                    + Objects.toString(curTask.getFirstLine(), "");

            CSVStatisticLoaderHelper.parseLine(from, to, sensorId, lineToParse, commonResult, Pair.of(0, 0L), false, 100);

            prevTask = curTask;
        }
        int sizeAfter = commonResult.size();
        log.info("Edge records added: {}", sizeAfter - sizeBefore);
    }

}
