package io.jmlim.modernjavainaction.chap04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.jmlim.modernjavainaction.chap04.Dish.menu;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class LowCaloricDishes {
    public static void main(String[] args) {
        java7();
        java8();
    }

    private static void java7() {
        List<Dish> lowCaloricDishes = new ArrayList<>();
        for (Dish dish : menu) {
            if (dish.getCalories() < 400) {
                lowCaloricDishes.add(dish);
            }
        }

        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish dish1, Dish dish2) {
                return Integer.compare(dish1.getCalories(), dish2.getCalories());
            }
        });

        List<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish dish : lowCaloricDishes) {
            lowCaloricDishesName.add(dish.getName());
        }

        System.out.println(lowCaloricDishesName);
    }

    private static void java8() {
        List<String> lowCaloricDishesName = menu.stream().filter(dish -> dish.getCalories() < 400)
                .sorted(comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(toList());

        System.out.println(lowCaloricDishesName);
    }

}
