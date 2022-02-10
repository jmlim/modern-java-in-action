package io.jmlim.modernjavainaction.chap05;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Quiz {
    public static void main(String[] args) {

        mapping();
//        fibonacci();
    }

    private static void mapping() {
        // mapping
        // 숫자 리스트가 주어졌을 때 각 숫자의 제곱근 리스트 반환
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> sqares = numbers.stream().map(n -> n * n)
                .collect(toList());
        System.out.println(sqares);

        // 두 개의 숫자 리스트가 있을 때 모든 숫자 쌍의 리스트를 반환
        // 1,2,3 과 3,4 가 주어지면 [(1,3), (1,4), (2,3), (2,4), (3,3), (3,4)] 를 반환
        List<Integer> numbers1 = Arrays.asList(1, 2, 3);
        List<Integer> numbers2 = Arrays.asList(3, 4);

        List<int[]> pairs = numbers1.stream()
                .flatMap(i -> numbers2.stream()
                        .map(j -> new int[]{i, j}))
                .collect(toList());
        for(int[] pair: pairs) {
            System.out.println(pair[0] + ", " + pair[1]);
        }
        System.out.println();

        // 3이 합으로 나누어 떨어지는 쌍만 반환
        List<int[]> pairs2 = numbers1.stream()
                .flatMap(i -> numbers2.stream()
                        .filter(j -> (i + j) % 3 == 0)
                        .map(j -> new int[]{i, j}))
                .collect(toList());
        for(int[] pair: pairs2) {
            System.out.println(pair[0] + ", " + pair[1]);
        }
    }

    private static void fibonacci() {
        //===============

        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(20)
                .forEach(t -> System.out.println("(" + t[0] + " , " + t[1] + ")"));

        // 첫번째 값만 추출
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .limit(20)
                .map(t -> t[0])
                .forEach(System.out::println);


        // stream of 1s with Stream.generate
        IntStream.generate(() -> 1)
                .limit(5)
                .forEach(System.out::println);
    }
}
