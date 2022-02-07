package io.jmlim.modernjavainaction.chap02;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.jmlim.modernjavainaction.chap02.Color.GREEN;
import static io.jmlim.modernjavainaction.chap02.Color.RED;

public class FilteringApples {
    public static void main(String[] args) {

        List<Apple> inventory = Arrays.asList(new Apple(80, GREEN), new Apple(155, GREEN), new Apple(120, RED));
        List<Apple> greenApples = filterGreenApples(inventory);
        System.out.println(greenApples);

        List<Apple> greenApples1 = filterApplesByColor(inventory, GREEN);
        System.out.println(greenApples1);

        List<Apple> redApples1 = filterApplesByColor(inventory, RED);
        System.out.println(redApples1);

        List<Apple> heavyApples = filterApplesByWeight(inventory, 150);
        System.out.println(heavyApples);

        // 2.1.3
        /*List<Apple> greenApples2 = filterApples(inventory, GREEN, 0, true);
        System.out.println(greenApples2);

        List<Apple> heavyApples2 = filterApples(inventory, null, 150, false);
        System.out.println(heavyApples2);*/

        List<Apple> greenApples3 = filterApples(inventory, (Apple apple) -> GREEN.equals(apple.getColor()));
        System.out.println(greenApples3);

        List<Apple> redApples = filter(inventory, apple -> RED.equals(apple.getColor()));
        System.out.println(redApples);

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
        System.out.println(evenNumbers);
    }

    /*    public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
            List<Apple> result = new ArrayList<>();
            for (Apple apple : inventory) {
                if ((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)) {
                    result.add(apple);
                }
            }
            return result;
        }*/
    public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }


    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getWeight() > weight) {
                result.add(apple);
            }
        }
        return result;
    }


    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (GREEN.equals(apple.getColor())) {
                result.add(apple);
            }
        }
        return result;
    }


    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }
}
