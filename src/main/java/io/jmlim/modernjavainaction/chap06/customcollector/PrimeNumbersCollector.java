package io.jmlim.modernjavainaction.chap06.customcollector;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static io.jmlim.modernjavainaction.chap06.customcollector.Prime.isPrime;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class PrimeNumbersCollector implements Collector<Integer,
        Map<Boolean, List<Integer>>, // 누적자 형식
        Map<Boolean, List<Integer>>> {  // 수집 연산의 결과 형식

    /**
     * 누적자를 만드는 함수 반환
     *
     * @return
     */
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<>() {{
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), candidate)) // isPrime 의 결과에 따라 소수 리스트와 비소수 리스트를 만듬
                    .add(candidate);  // candidate 를 알맞은 리스트에 추가.
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
            map1.get(true).addAll(map2.get(true));
            map1.get(false).addAll(map2.get(false));
            return map1;
        };
    }

    /**
     * 변환과정이 필요 없으므로 항등함수 반환
     *
     * @return
     */
    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        // 발견한 소수의 순서에 의미가 있으므로 IDENITTY_FINISH 지만 UNORDERED , CONCURRENT  는 아니다.
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }
}
