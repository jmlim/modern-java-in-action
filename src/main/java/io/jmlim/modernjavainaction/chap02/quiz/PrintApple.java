package io.jmlim.modernjavainaction.chap02.quiz;

import io.jmlim.modernjavainaction.chap02.Apple;

import java.util.Arrays;
import java.util.List;

import static io.jmlim.modernjavainaction.chap02.Color.GREEN;
import static io.jmlim.modernjavainaction.chap02.Color.RED;

public class PrintApple {
    public static void main(String[] args) {
        List<Apple> inventory = Arrays.asList(new Apple(80, GREEN), new Apple(155, GREEN), new Apple(120, RED));

        prettyPrintApple(inventory, new AppleFancyFormatter());

        System.out.println("=======");

        prettyPrintApple(inventory, new AppleSimpleFormatter());


    }

    public static void prettyPrintApple(List<Apple> inventory, AppleFormatter af) {
        for (Apple apple : inventory) {
            String output = af.accept(apple);
            System.out.println(output);
        }
    }
}
