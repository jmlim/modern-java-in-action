package io.jmlim.modernjavainaction.chap09.chainofresponsibility;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Main {
    public static void main(String[] args) {
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);

        String result = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result);


        System.out.println("======");
        // 람다
        UnaryOperator<String> headerProcessing = (String input) -> "From Raoul, Mario and Alan: " + input;
        UnaryOperator<String> spellCheckerProcessing = (String input) -> input.replaceAll("ladba", "lambda");

        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        String result1 = pipeline.apply("Aren't labdas really sexy?!!");
        System.out.println(result1);

    }
}
