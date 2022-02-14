package io.jmlim.modernjavainaction.chap06.customcollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.jmlim.modernjavainaction.chap06.customcollector.Prime.isPrime;
import static java.util.stream.Collectors.partitioningBy;

public class PrimeNumbersCollectorExample {
    public static void main(String[] args) {
        Map<Boolean, List<Integer>> primeNumbers = partitionPrimesWithCustomCollector(100);
        System.out.println(primeNumbers);
    }

    public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(new PrimeNumbersCollector());
    }

    public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(partitioningBy(Prime::isPrime));
    }

    public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollectorNotCollectorClass(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(() -> new HashMap<Boolean, List<Integer>>() {{
                            put(true, new ArrayList<>());
                            put(false, new ArrayList<>());
                        }}, (acc, candidate) -> {
                            acc.get(isPrime(acc.get(true), candidate)).add(candidate);
                        }, (map1, map2) -> {
                            map1.get(true).addAll(map2.get(true));
                            map1.get(false).addAll(map2.get(false));
                        }
                );
    }

}
