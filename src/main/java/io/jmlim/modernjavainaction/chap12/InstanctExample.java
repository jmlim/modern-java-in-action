package io.jmlim.modernjavainaction.chap12;

import java.time.Instant;
import java.time.temporal.ChronoField;

public class InstanctExample {
    public static void main(String[] args) {
        Instant instant = Instant.ofEpochSecond(3);
        System.out.println(instant);

        Instant instant1 = Instant.ofEpochSecond(3, 0);
        System.out.println(instant1);
        Instant instant2 = Instant.ofEpochSecond(2, 1_000_000_000);// 2초 이후의 1억 나노초
        System.out.println(instant2);
        Instant instant3 = Instant.ofEpochSecond(4, -1_000_000_000); // 4초 이전의 1억 나노초
        System.out.println(instant3);

        int day = Instant.now().get(ChronoField.DAY_OF_MONTH);
        System.out.println(day);
    }
}
