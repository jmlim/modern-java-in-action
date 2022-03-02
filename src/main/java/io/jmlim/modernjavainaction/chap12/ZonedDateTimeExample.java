package io.jmlim.modernjavainaction.chap12;

import java.time.*;

public class ZonedDateTimeExample {
    public static void main(String[] args) {
        ZoneId romeZone = ZoneId.of("Europe/Rome");
        LocalDate date = LocalDate.of(2020, Month.MARCH, 02);
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);
        System.out.println(zdt1);

        LocalDateTime dateTime = LocalDateTime.of(2020, Month.MARCH, 18, 13, 45);
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);
        System.out.println(zdt2);

        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);
        System.out.println(zdt3);

        LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);
        System.out.println(timeFromInstant);
    }
}
