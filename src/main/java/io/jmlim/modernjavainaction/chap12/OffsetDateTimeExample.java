package io.jmlim.modernjavainaction.chap12;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class OffsetDateTimeExample {
    public static void main(String[] args) {
        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
        LocalDateTime dateTime = LocalDateTime.of(2020, Month.MARCH, 18, 13, 45);
        OffsetDateTime dateTimeInNewWork = OffsetDateTime.of(dateTime, newYorkOffset);
        System.out.println(dateTimeInNewWork);
    }
}
