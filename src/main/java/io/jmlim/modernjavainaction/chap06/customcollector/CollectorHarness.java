package io.jmlim.modernjavainaction.chap06.customcollector;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CollectorHarness {
    public static void main(String[] args) {
        speedTest(PrimeNumbersCollectorExample::partitionPrimes);
        speedTest(PrimeNumbersCollectorExample::partitionPrimesWithCustomCollector);
        speedTest(PrimeNumbersCollectorExample::partitionPrimesWithCustomCollectorNotCollectorClass);
    }

    private static void speedTest(Function<Integer, Map<Boolean, List<Integer>>> function) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();

            function.apply(1_000_000);

            long duration = (System.nanoTime() - start) / 1_000_000;
            if (duration < fastest) fastest = duration;
        }
        System.out.println("Fastest execution done in " + fastest + " msecs");
    }
}
