package io.jmlim.modernjavainaction.chap06;

import java.util.*;

import static io.jmlim.modernjavainaction.chap06.Dish.dishTags;
import static io.jmlim.modernjavainaction.chap06.Dish.menu;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

public class GroupingJmExam {
    public enum CaloricLevel {DIET, NORMAL, FAT}

    ;

    public static void main(String[] args) {
        Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
                groupingBy(dish -> {
                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                    else return CaloricLevel.FAT;
                })
        );

        System.out.println(dishesByCaloricLevel);

        System.out.println("==");

        Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream().filter(d -> d.getCalories() > 500)
                .collect(groupingBy(Dish::getType));
        System.out.println(caloricDishesByType);

        Map<Dish.Type, List<Dish>> caloricDishesByType2 = menu.stream().collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));
        System.out.println(caloricDishesByType2);

        Map<Dish.Type, List<String>> dishNamesByType = menu.stream()
                .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));

        System.out.println(dishNamesByType);

        Map<Dish.Type, Set<String>> dishNamesByType2 = menu.stream().collect(
                groupingBy(Dish::getType,
                        flatMapping(dish -> dishTags.get(dish.getName()).stream()
                                , toSet()))
        );
        System.out.println(dishNamesByType2);

        // 다수준 그룹화
        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> disheByTypeCaloricLevel = menu.stream().collect(
                groupingBy(Dish::getType, // 첫 번째 수준의 분류 함수
                        groupingBy(dish -> { // 두 번째 수준의 분류 함수
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }))
        );
        System.out.println(disheByTypeCaloricLevel);

        // 6.3.3 서브그룹으로 데이터 수집
        Map<Dish.Type, Long> typesCount = menu.stream()
                .collect(
                        groupingBy(Dish::getType, counting())
                );
        System.out.println(typesCount);

        //
        Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream().collect(
                groupingBy(Dish::getType,
                        maxBy(comparingInt(Dish::getCalories)))
        );
        System.out.println(mostCaloricByType);

        // 각 서브그룹에서 가장 칼로리가 높은 요리 찾기.
        Map<Dish.Type, Dish> mostCaloricByType2 = menu.stream()
                .collect(groupingBy(Dish::getType, // 분류함수
                        collectingAndThen(
                                maxBy(comparingInt(Dish::getCalories)), // 감싸인 컬렉터
                                Optional::get // 변환함수
                        )
                ));
        System.out.println(mostCaloricByType2);

        Map<Dish.Type, Integer> totalCaloriesByType = menu.stream().collect(
                groupingBy(Dish::getType,
                        summingInt(Dish::getCalories))
        );
        System.out.println(totalCaloriesByType);

        //각 요리 형식에 존재하는 모든 CaloricLevel 값을 알고 싶다고 가정할 때
        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream().collect(
                groupingBy(Dish::getType,
                        mapping(dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }, toSet()))
        );
        System.out.println(caloricLevelsByType);

        // toCollection을 이용하면 원하는 방식으로 결과를 제어할 수 있다.
        Map<Dish.Type, HashSet<CaloricLevel>> caloricLevelsByType2 = menu.stream().collect(
                groupingBy(Dish::getType,
                        mapping(dish -> {
                            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                            else return CaloricLevel.FAT;
                        }, toCollection(HashSet::new)))
        );
        System.out.println(caloricLevelsByType2);

        // 파티셔닝 (채식과 채직이 아닌 요리로 분류)
        Map<Boolean, List<Dish>> partitionedMenu = menu.stream()
                .collect(partitioningBy(Dish::isVegetarian)); // 분할 함수

        System.out.println(partitionedMenu);

        // 채식 요리 와 채식이 아닌 요리 각각의 그룹에서 칼로리가 가장 높은 요리 찾기.
        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream()
                .collect(partitioningBy(Dish::isVegetarian, collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));
        System.out.println(mostCaloricPartitionedByVegetarian);


        // 다수준 분할
        Map<Boolean, Map<Boolean, List<Dish>>>
        VegetarianFoodWithOver500Calories = menu.stream().collect(partitioningBy(Dish::isVegetarian,
                partitioningBy(d -> d.getCalories() > 500)));
        System.out.println(VegetarianFoodWithOver500Calories);

        Map<Boolean, Long> vegetarianCnt = menu.stream().collect(partitioningBy(Dish::isVegetarian, counting()));
        System.out.println(vegetarianCnt);
    }
}
