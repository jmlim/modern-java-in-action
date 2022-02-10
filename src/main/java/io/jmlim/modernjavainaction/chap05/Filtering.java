package io.jmlim.modernjavainaction.chap05;

import io.jmlim.modernjavainaction.chap04.Dish;

import static io.jmlim.modernjavainaction.chap04.Dish.menu;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public class Filtering {

    public static void main(String... args) {
        // Filtering with predicate
        System.out.println("Filtering with a predicate");
        List<Dish> vegetarianMenu = menu.stream()
                .filter(Dish::isVegetarian)
                .collect(toList());
        vegetarianMenu.forEach(System.out::println);

        System.out.println();
        // Filtering unique elements
        System.out.println("Filtering unique elements:");
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);

        // Slicing a stream
        // This list is sorted in ascending order of number of calories!
        List<Dish> specialMenu = Arrays.asList(
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER));
        System.out.println();
        System.out.println("Filtered sorted menu:");
        List<Dish> filteredMenu = specialMenu.stream()
                .filter(dish -> dish.getCalories() < 320)
                .collect(toList());
        filteredMenu.forEach(System.out::println);

        System.out.println();

        System.out.println("Sorted menu sliced with takeWhile():");
        List<Dish> slicedMenu1 = specialMenu.stream()
                .takeWhile(dish -> dish.getCalories() < 320)
                .collect(toList());
        slicedMenu1.forEach(System.out::println);

        System.out.println();

        System.out.println("Sorted menu sliced with dropWhile():");
        List<Dish> slicedMenu2 = specialMenu.stream()
                .dropWhile(dish -> dish.getCalories() < 320)
                .collect(toList());
        slicedMenu2.forEach(System.out::println);

        System.out.println();
        // Truncating a stream
        List<Dish> dishesLimit3 = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .limit(3)
                .collect(toList());
        System.out.println("Truncating a stream:");
        dishesLimit3.forEach(System.out::println);

        System.out.println();
        // Skipping elements
        List<Dish> dishesSkip2 = menu.stream()
                .filter(d -> d.getCalories() > 300)
                .skip(2)
                .collect(toList());
        System.out.println("Skipping elements:");
        dishesSkip2.forEach(System.out::println);

        System.out.println();
        // 스트림을 이용해서 처음 등장하는 두 고기 요리 필터링
        List<Dish> dishes = menu.stream()
                .filter(d -> d.getType() == Dish.Type.MEAT)
                .limit(2)
                .collect(toList());
       dishes.forEach(System.out::println);
    }
}
