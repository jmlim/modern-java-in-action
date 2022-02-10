package io.jmlim.modernjavainaction.chap06;

import java.util.Comparator;
import java.util.IntSummaryStatistics;
import java.util.Optional;

import static io.jmlim.modernjavainaction.chap06.Dish.menu;
import static java.util.stream.Collectors.*;

public class DishExample {
    public static void main(String[] args) {
        Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
        Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));
        System.out.println(mostCalorieDish);

        int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
        System.out.println(totalCalories);

        IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
        System.out.println(menuStatistics);

        String shortMenu = menu.stream().map(Dish::getName).collect(joining());
        System.out.println(shortMenu);

        shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
        System.out.println(shortMenu);

        int totalCalories1 = menu.stream().collect(reducing(0, Dish::getCalories, (a, b) -> a + b));
        System.out.println(totalCalories1);

        Optional<Dish> mostCalorieDish1 = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
        System.out.println(mostCalorieDish1);

    }
}
