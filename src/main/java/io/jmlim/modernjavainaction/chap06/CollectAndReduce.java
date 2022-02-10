package io.jmlim.modernjavainaction.chap06;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CollectAndReduce {
    public static void main(String[] args) {
        Stream<Integer> stream = Arrays.asList(1, 2, 3, 4, 5, 6).stream();
        // 아래 예제는 reduce 메서드는 누적자로 사용된 리스트를 변환시키므로 reduce 를 잘못 활용한 예에 해당된다.
        // 여러 스레드가 동시에 같은 데이터 구조체를 고치면 리스트 자체가 망가져버리므로 리듀싱 연산을 병렬로 수행할 수 없다는 점도 문제
        List<Integer> numbers = stream.reduce(new ArrayList<>(), (List<Integer> l, Integer e) -> {
            l.add(e);
            return l;
        }, (List<Integer> l1, List<Integer> l2) -> {
            l1.addAll(l2);
            return l1;
        });
        System.out.println(numbers);
    }
}
