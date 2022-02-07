package io.jmlim.modernjavainaction.chap02.quiz;

import io.jmlim.modernjavainaction.chap02.Apple;

public class AppleSimpleFormatter implements AppleFormatter {
    @Override
    public String accept(Apple apple) {
        return "An apple of " + apple.getWeight() + "g";
    }
}
