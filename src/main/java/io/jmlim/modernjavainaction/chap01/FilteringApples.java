package io.jmlim.modernjavainaction.chap01;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FilteringApples {
    public static void main(String[] args) {
        List<Apple> apples = Arrays.asList(new Apple(80, "green")
                , new Apple(155, "green")
                , new Apple(120, "red"));

        // [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
        List<Apple> greenApples = filterApples(apples, FilteringApples::isGreenApple);
        System.out.println(greenApples);

        // [Apple{color='green', weight=155}]
        List<Apple> heavyApples = filterApples(apples, FilteringApples::isHeavyApple);
        System.out.println(heavyApples);

        // [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
        List<Apple> greenApples2 = filterApples(apples, (Apple a) -> "green".equals(a.getColor()));
        System.out.println(greenApples2);

        // [Apple{color='green', weight=155}]
        List<Apple> heavyApples2 = filterApples(apples, (Apple a) -> a.getWeight() > 150);
        System.out.println(heavyApples2);

        // []
        List<Apple> weirdApples = filterApples(apples, (Apple a) -> a.getWeight() < 80 || "brown".equals(a.getColor()));
        System.out.println(weirdApples);
    }

    private static boolean isHeavyApple(Apple apple) {
        return apple.getWeight() > 150;
    }

    private static boolean isGreenApple(Apple apple) {
        return apple.getColor().equals("green");
    }

    public static List<Apple> filterApples(List<Apple> apples, Predicate<Apple> p) {
        List<Apple> filterApples = new ArrayList<>();
        for (Apple apple : apples) {
            if (p.test(apple)) {
                filterApples.add(apple);
            }
        }
        return filterApples;
    }
}
