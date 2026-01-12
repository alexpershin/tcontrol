package org.tcontrol.service.impl;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tcontrol.models.SensorValue;
import org.tcontrol.service.HistoricDataCache;
import org.tcontrol.service.IStatisticsLoader;

import java.io.FileNotFoundException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class HistoricDataCacheImp implements HistoricDataCache {
    private static int MAX_RANGES_TO_LOAD = 3;

    private final IStatisticsLoader csvStatisticsLoader;

    private final RangeSet<Long> rangeSet = TreeRangeSet.create();
    private final Map<Integer, TreeMap<Long, SensorValue>> cache = new HashMap<>();

    @Override
    public Collection<SensorValue> historyValues(long from, long to, Integer sensorId) throws FileNotFoundException {
        Range<Long> range = Range.open(from, to);
        if (rangeSet.encloses(range)) {
            return cache.get(sensorId).subMap(from, to).values();
        } else {
            RangeSet<Long> sub = rangeSet.complement().subRangeSet(range);
            Set<Range<Long>> ranges = sub.asRanges();
            if (ranges.size() > MAX_RANGES_TO_LOAD) {
                final List<SensorValue> values = csvStatisticsLoader.load(from, to, sensorId);
                addToCache(values, sensorId);
                rangeSet.add(range);
            } else {
                for (Range<Long> rangeToLoad : ranges) {
                    final List<SensorValue> values = csvStatisticsLoader.load(rangeToLoad.lowerEndpoint(), rangeToLoad.upperEndpoint(), sensorId);
                    addToCache(values, sensorId);
                    rangeSet.add(rangeToLoad);
                }
            }
            return cache.get(sensorId).subMap(from, to).values();
        }
    }

    private void addToCache(List<SensorValue> values, Integer sensorId) {
        TreeMap<Long, SensorValue> sensorValues = cache.computeIfAbsent(sensorId, k -> new TreeMap<>());
        values.forEach(value -> sensorValues.put(value.getTimestamp(), value));
    }


}
