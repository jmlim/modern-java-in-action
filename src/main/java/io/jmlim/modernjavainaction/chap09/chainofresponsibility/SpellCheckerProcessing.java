package io.jmlim.modernjavainaction.chap09.chainofresponsibility;

public class SpellCheckerProcessing extends ProcessingObject<String> {
    @Override
    public String handleWork(String input) {
        return input.replaceAll("ladba", "lambda");
    }
}
