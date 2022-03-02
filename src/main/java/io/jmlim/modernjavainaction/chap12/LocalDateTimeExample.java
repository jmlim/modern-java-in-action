package io.jmlim.modernjavainaction.chap12;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

public class LocalDateTimeExample {
    public static void main(String[] args) {
        LocalDateTime dt1 = LocalDateTime.of(1986, Month.AUGUST, 30, 03, 30, 33);
        System.out.println(dt1);
        LocalDateTime dt2 = LocalDateTime.of(LocalDate.of(2021, Month.FEBRUARY, 19), LocalTime.of(07, 50, 55));
        System.out.println(dt2);

        LocalDate date = LocalDate.now();
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        LocalTime time = LocalTime.of(11, 22, 33);
        LocalDateTime dt4 = time.atDate(date);
        System.out.println(dt3);
        System.out.println(dt4);

        LocalDate localDate = dt1.toLocalDate();
        System.out.println(localDate);
        LocalTime localTime = dt1.toLocalTime();
        System.out.println(localTime);
    }
}
