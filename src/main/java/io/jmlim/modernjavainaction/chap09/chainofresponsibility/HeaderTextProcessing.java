package io.jmlim.modernjavainaction.chap09.chainofresponsibility;

public class HeaderTextProcessing extends ProcessingObject<String> {
    @Override
    public String handleWork(String input) {
        return "From Raoul, Mario and Alan: " + input;
    }
}
