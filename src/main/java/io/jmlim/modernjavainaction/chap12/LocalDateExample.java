package io.jmlim.modernjavainaction.chap12;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class LocalDateExample {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2022, 03, 02);

        localDate(date);
        System.out.println("====");
        temporalField(date);
        System.out.println("====");
        parseDateOrTime();
        System.out.println("====");

        LocalDate date1 = LocalDate.of(2020, 01, 02);
        LocalDate date2 = date1.withYear(2022);
        System.out.println(date2);
        LocalDate date3 = date2.withDayOfMonth(25);
        System.out.println(date3);
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);
        System.out.println(date4);

        System.out.println("====");
        LocalDate date5 = LocalDate.of(2020, 01, 02);
        LocalDate date6 = date5.plusWeeks(1);
        System.out.println(date6);
        LocalDate date7 = date6.minusYears(6);
        System.out.println(date7);
        LocalDate date8 = date7.plus(6, ChronoUnit.MONTHS);
        System.out.println(date8);


    }

    private static void parseDateOrTime() {
        LocalDate parseDate = LocalDate.parse("2021-03-01");
        System.out.println(parseDate);
        LocalTime parseTime = LocalTime.parse("11:41:22");
        System.out.println(parseTime);
    }

    private static void localDate(LocalDate date) {

        System.out.println(date);

        int year = date.getYear();
        Month month = date.getMonth();
        int day = date.getDayOfMonth();
        DayOfWeek dow = date.getDayOfWeek();
        int len = date.lengthOfMonth();
        boolean leap = date.isLeapYear(); // 윤년 여부

        System.out.println(year);
        System.out.println(month);
        System.out.println(day);
        System.out.println(dow);
        System.out.println(len);
        System.out.println(leap);


    }

    private static void temporalField(LocalDate date) {
        int year = date.get(ChronoField.YEAR);
        int month = date.get(ChronoField.MONTH_OF_YEAR);
        int day = date.get(ChronoField.DAY_OF_MONTH);
        System.out.println(year);
        System.out.println(month);
        System.out.println(day);
    }
}
