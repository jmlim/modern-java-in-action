package io.jmlim.modernjavainaction.chap06.customcollector;

import io.jmlim.modernjavainaction.chap06.Dish;

import java.util.ArrayList;
import java.util.List;

import static io.jmlim.modernjavainaction.chap06.Dish.menu;
import static java.util.stream.Collectors.toList;

public class ToListCollectorExample {
    public static void main(String[] args) {
        List<Dish> dishes = menu.stream().collect(new ToListCollector<>());
        System.out.println(dishes);

        List<Dish> dishes1 = menu.stream().collect(toList());
        System.out.println(dishes1);

        // 컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기.
        List<Dish> dishes2 = menu.stream().collect(ArrayList::new,
                List::add,
                List::addAll);
        System.out.println(dishes2);
    }
}
