package io.jmlim.modernjavainaction.chap09.strategy;

public class IsNumeric implements ValidationStrategy {
    @Override
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}
