package io.jmlim.modernjavainaction.chap12;

import java.time.*;

public class DurationPeriodExample {
    public static void main(String[] args) {
        LocalTime time1 = LocalTime.of(11, 22, 33);
        LocalTime time2 = LocalTime.of(12, 22, 33);
        Duration d1 = Duration.between(time1, time2);
        System.out.println(d1);

        LocalDateTime localDateTime1 = LocalDateTime.of(2020, 01, 01, 11, 22, 33);
        LocalDateTime localDateTime2 = LocalDateTime.of(2020, 02, 01, 11, 23, 34);
        Duration d2 = Duration.between(localDateTime1, localDateTime2);
        System.out.println(d2);

        Period tenDays = Period.between(LocalDate.of(2020, 01, 01)
                , LocalDate.of(2020, 01, 11));
        System.out.println(tenDays);



    }
}
