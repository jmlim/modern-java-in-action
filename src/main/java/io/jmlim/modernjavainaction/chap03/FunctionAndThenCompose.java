package io.jmlim.modernjavainaction.chap03;

import java.util.function.Function;

/**
 * ### 3.8.3. Function 조합
 * - andThen - 이후에 처리할 function 추가
 * - compose - 이전에 처리되어야할 function 추가
 */
public class FunctionAndThenCompose {
    public static void main(String[] args) {
        andThen();
        compose();
    }

    private static void andThen() {
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        Function<Integer, Integer> h = f.andThen(g);
        int result = h.apply(1); // 4를 반환
        System.out.println(result);
    }

    private static void compose() {
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        Function<Integer, Integer> h = f.compose(g);
        int result = h.apply(1); // 3를 반환
        System.out.println(result);
    }
}
