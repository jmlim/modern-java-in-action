package io.jmlim.modernjavainaction.chap07;

import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ParallelStreamsExample {
    public static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
                .limit(n) // n개로 제한
                .reduce(0L, Long::sum);
    }

    public static long iterativeSum(long n) {
        long result = 0;
        for (long i = 0; i <= n; i++) {
            result += i;
        }
        return result;
    }

    public static long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
                .limit(n) // n개로 제한
                .parallel() // 스트림을 병렬 스트림으로 변환
                .reduce(0L, Long::sum);
    }


    public static void main(String[] args) {
        long sequentialSum = sequentialSum(10);
        System.out.println(sequentialSum);

        long iterativeSum = iterativeSum(10);
        System.out.println(iterativeSum);

        long parallelSum = parallelSum(10);
        System.out.println(parallelSum);

        long sideEffectSum = sideEffectSum(10_000_000L);
        System.out.println(sideEffectSum);
    }

    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }


    public static long parallelRangedSum(long n) {
        return LongStream.rangeClosed(1, n).parallel().reduce(Long::sum).getAsLong();
    }


    public static long sideEffectParallelSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }


    public static long rangedSum(long n) {
        return LongStream.rangeClosed(1, n).reduce(Long::sum).getAsLong();
    }

    private static class Accumulator {
        public long total = 0;

        public void add(long value) {
            total += value;
        }
    }
}
