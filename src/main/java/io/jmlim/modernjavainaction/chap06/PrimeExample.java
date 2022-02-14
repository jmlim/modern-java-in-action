package io.jmlim.modernjavainaction.chap06;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.partitioningBy;

public class PrimeExample {
    /*public boolean isPrime(int candidate) {
        return IntStream.range(2, candidate)
                .noneMatch(i -> candidate % i == 0); // 스트림의 모든 정수로 candidate를 나눌 수 없으면 참을 반환
    }*/

    public boolean isPrime(int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }

    public Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(partitioningBy(this::isPrime));
    }

    public static void main(String[] args) {
        PrimeExample example = new PrimeExample();
        boolean prime = example.isPrime(5);
        System.out.println(prime);

        Map<Boolean, List<Integer>> booleanListMap = example.partitionPrimes(100);
        System.out.println(booleanListMap);
    }


}
