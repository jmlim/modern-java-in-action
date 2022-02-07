package io.jmlim.modernjavainaction.chap01;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class FilteringApplesStream {
    public static void main(String[] args) {
        List<Apple> apples = Arrays.asList(new Apple(80, "green")
                , new Apple(155, "green")
                , new Apple(120, "red"));

        // [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
        List<Apple> greenApples = apples.stream().filter(apple -> apple.getColor().equals("green")).collect(toList());
        System.out.println(greenApples);

        //[Apple{color='green', weight=155}]
        List<Apple> heavyApples = apples.stream().filter(apple -> apple.getWeight() > 150).collect(toList());
        System.out.println(heavyApples);

        //[]
        List<Apple> weirdApples = apples.stream().filter(apple -> apple.getWeight() < 80 || "brown".equals(apple.getColor())).collect(toList());
        System.out.println(weirdApples);
    }
}
