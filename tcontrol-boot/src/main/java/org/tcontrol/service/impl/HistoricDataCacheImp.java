package org.tcontrol.service.impl;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tcontrol.models.SensorValueShort;
import org.tcontrol.service.HistoricDataCache;
import org.tcontrol.service.IStatisticsLoader;

import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoricDataCacheImp implements HistoricDataCache {
    private static int MAX_RANGES_TO_LOAD = 3;

    private final IStatisticsLoader csvStatisticsLoader;

    private final Map<Integer, RangeSet<Long>> rangeSetBySensor = new HashMap<>();
    private final Map<Integer, SoftReference<ConcurrentSkipListMap<Long, SensorValueShort>>> cache = new ConcurrentHashMap<>();

    @Override
    public Collection<SensorValueShort> historyValues(long from, long to, Integer sensorId) throws FileNotFoundException {
        Range<Long> range = Range.open(from, to);
        RangeSet<Long> rangeSet = rangeSetBySensor.computeIfAbsent(sensorId, e -> TreeRangeSet.create());

        ConcurrentSkipListMap<Long, SensorValueShort> sensorTree = cache.computeIfAbsent(sensorId, k -> new SoftReference<>(new ConcurrentSkipListMap<>())).get();

        if (sensorTree != null) {
            if (rangeSet.encloses(range)) {
                Collection<SensorValueShort> values = sensorTree.subMap(from, to).values();
                log.info("values extracted from cache: {}", values.size());
            }
        } else {
            rangeSetBySensor.remove(sensorId);
        }

        rangeSet = rangeSetBySensor.computeIfAbsent(sensorId, e -> TreeRangeSet.create());
        {
            RangeSet<Long> sub = rangeSet.complement().subRangeSet(range);
            Set<Range<Long>> ranges = sub.asRanges();
            if (ranges.size() > MAX_RANGES_TO_LOAD) {
                final List<SensorValueShort> values = csvStatisticsLoader.load(from, to, sensorId);
                addToCache(values, sensorId);
                rangeSet.add(range);
                return values;
            } else {
                for (Range<Long> rangeToLoad : new ArrayList<>(ranges)) {
                    final List<SensorValueShort> values
                            = csvStatisticsLoader.load(rangeToLoad.lowerEndpoint(), rangeToLoad.upperEndpoint(), sensorId);
                    addToCache(values, sensorId);
                    rangeSet.add(rangeToLoad);
                }
            }
            sensorTree = cache.get(sensorId).get();
            if (sensorTree == null) {
                throw new RuntimeException("can't get life cache");
            }

            return sensorTree.subMap(from, to).values();
        }
    }

    private void addToCache(List<SensorValueShort> values, Integer sensorId) {
        ConcurrentSkipListMap<Long, SensorValueShort> longSensorValueTreeMap =
                cache.computeIfAbsent(sensorId, k -> new SoftReference<>(new ConcurrentSkipListMap<>())).get();

        if (longSensorValueTreeMap == null) {
            longSensorValueTreeMap = new ConcurrentSkipListMap<>();
            SoftReference<ConcurrentSkipListMap<Long, SensorValueShort>> value = new SoftReference<>(longSensorValueTreeMap);
            cache.put(sensorId, value);
        }

        for (SensorValueShort value : values) {
            longSensorValueTreeMap.put(value.getTimestamp(), value);
        }
    }
}
