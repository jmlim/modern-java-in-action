package io.jmlim.modernjavainaction.chap12;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.*;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

public class TemporalAdjustersExample {
    public static void main(String[] args) {
        LocalDate date1 = LocalDate.of(2020, 3, 18);
        LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY)); // 2020-03-23
        System.out.println(date2);
        LocalDate date3 = date2.with(lastDayOfMonth()); // 2020-03-31
        System.out.println(date3);


        quiz12_2();

        quiz12_2_lambda();

    }

    private static void quiz12_2_lambda() {
        LocalDateTime date = LocalDateTime.of(2022, 03, 04, 14, 42, 05);
        // 만일 TemporalAdjuster 를 람다 표현식으로 정의하고 싶다면 다음 코드에서 보여주는 것처럼 UnaryOperator<LocalDate> 를 인수로 받는 TemporalAdjusters 클래스의 정적 팩토리 ofDateAdjuster 를 사용하는 것이 좋다.
        TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(temporal -> {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if (dow == DayOfWeek.FRIDAY) {
                dayToAdd = 3;
            } else if (dow == DayOfWeek.SATURDAY) {
                dayToAdd = 2;
            }
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });

        LocalDateTime withDate = date.with(nextWorkingDay);
        System.out.println(withDate);
    }

    private static void quiz12_2() {
        LocalDateTime date = LocalDateTime.of(2022, 03, 04, 14, 42, 05);
        LocalDateTime withDate = date.with(new NextWorkingDay());
        System.out.println(withDate);
    }

    private static class NextWorkingDay implements TemporalAdjuster {
        @Override
        public Temporal adjustInto(Temporal temporal) {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if (dow == DayOfWeek.FRIDAY) {
                dayToAdd = 3;
            } else if (dow == DayOfWeek.SATURDAY) {
                dayToAdd = 2;
            }
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        }
    }
}
