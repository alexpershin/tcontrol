package org.tcontrol.poc;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.TreeRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;


public class PocCaffeine {

    // 1. Определяем класс для представления диапазона (ключ)
// Используем record для краткости, он автоматически реализует equals/hashCode
    @Data
    static class RangeKey {
        private final int start;
        private final int end;

        // Убедимся, что диапазон корректен при создании
        public RangeKey(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException("Start cannot be greater than end");
            }
            this.start = start;
            this.end = end;
        }
    }

    public static void main(String[] args) {
        // 2. Создаем кэш с помощью builder
        LoadingCache<RangeKey, List<Integer>> dataCache = Caffeine.newBuilder()
                // Максимальное количество записей
                .maximumSize(1000)
                // Истечение срока действия записи через 10 минут после создания
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // Истечение срока действия записи через 5 минут после последнего доступа
                .expireAfterAccess(5, TimeUnit.MINUTES)
                // Кешируем до 50% записей, когда кэш почти полон
                .weakValues() // Позволяет сборщику мусора удалять значения
                .build(key -> {
                    // 3. Функция загрузки данных:
                    // Это будет вызываться, когда ключ еще не в кэше.
                    System.out.println("Loading data for rangeSet: [" + key.getStart() + ", " + key.getEnd() + "]");
                    // Имитация длительной операции:
                    try {
                        Thread.sleep(100); // Например, запрос к БД или вычисления
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    // Возвращаем результат (например, сумму чисел в диапазоне)
                    return generate(key.getStart(), key.getEnd());
                });

        // 4. Использование кэша
//        RangeKey range1 = new RangeKey(1, 10);
//        RangeKey range2 = new RangeKey(11, 20);
//        RangeKey range1Again = new RangeKey(1, 10); // Тот же ключ

        // Первый вызов - загрузка и кэширование
        System.out.println("Result 1: " + dataCache.get(new RangeKey(1, 10)));

        // Второй вызов - получение из кэша
        System.out.println("Result 2: " + dataCache.get(new RangeKey(11, 20)));

        // Еще один диапазон
        System.out.println("Result 3: " + dataCache.get( new RangeKey(1, 10)));
        System.out.println("Result 4: " + dataCache.get(new RangeKey(2, 11)));

        // Получим размер кэша
        System.out.println("Cache size: " + dataCache.estimatedSize());

        RangeSet<Integer> rangeSet = merge();
        System.out.println("rangeSet : " + rangeSet);
        System.out.println("Enclosed: " + rangeSet.encloses(Range.open(4, 11)));
        System.out.println("Not enclosed: " + rangeSet.encloses(Range.open(0, 2)));
        System.out.println("complement: " + rangeSet.complement());
        System.out.println("span: " + rangeSet.span());
    //    System.out.println("intersects: " + rangeSet.intersects(Range.open(1, 2)));
        System.out.println("original: " + rangeSet);
        System.out.println("sub: " + rangeSet.complement().subRangeSet(Range.open(10, 35)));
        System.out.println("size: " + rangeSet.asRanges().size());

        test2();
    }

    // Вспомогательная функция для имитации вычислений
    private static List<Integer> generate(int start, int end) {
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            array.add(i);
        }
        return array;
    }

    private static RangeSet<Integer> merge(){
        RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10)); // {[1, 10]}
        rangeSet.add(Range.closed(5, 15)); // disconnected range; {[1, 10], [11, 15)}
        rangeSet.add(Range.closed(15, 20)); // connected range; {[1, 10], [11, 20)}
        rangeSet.add(Range.closed(30, 40)); // connected range; {[1, 10], [11, 20)}
        rangeSet.add(Range.closed(20, 30)); // connected range; {[1, 10], [11, 20)}
     //   rangeSet.remove(Range.open(5, 10));



        return rangeSet;
    }

    public static void test2() {
        RangeSet<Integer> rangeSet = TreeRangeSet.create();

        // Add some ranges to the set (e.g., [1..5] and [10..15])
        rangeSet.add(Range.closed(1, 5));
        rangeSet.add(Range.closed(10, 15));

        // Define the reference range you want to check for gaps (e.g., [0..20])
        Range<Integer> referenceRange = Range.closed(0, 20);

        // Get the parts of the reference range not included in the rangeSet
        RangeSet<Integer> gapsInRange = rangeSet.complement().subRangeSet(referenceRange);

        // Get the individual Range objects representing the gaps
        Set<Range<Integer>> exclusiveRanges = gapsInRange.asRanges();

        // Print the results
        System.out.println("Original RangeSet ranges: " + rangeSet.asRanges());
        System.out.println("Reference Range: " + referenceRange);
        System.out.println("Ranges within the reference range NOT covered: " + exclusiveRanges);

        // Example output for the above:
        // Original RangeSet ranges: [[1..5], [10..15]]
        // Reference Range: [0..20]
        // Ranges within the reference range NOT covered: [[0..1), (5..10), (15..20]]
    }

}
