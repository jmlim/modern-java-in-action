package io.jmlim.modernjavainaction.chap12;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class DateTimeFormatterExample {
    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2020, 03, 02);
        String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println(s1);
        System.out.println(s2);

        System.out.println("===");
        LocalDate date1 = LocalDate.parse("20200302", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate date2 = LocalDate.parse("2020-03-02", DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.println(date1);
        System.out.println(date2);


        // 패턴으로 DateTimeFormatter 만들기
        System.out.println("===");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date3 = LocalDate.of(2020, 03, 02);
        String formattedDate = date3.format(formatter);
        System.out.println(formattedDate);
        LocalDate date4 = LocalDate.parse(formattedDate, formatter);
        System.out.println(date4);

        System.out.println("===");
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        LocalDate date5 = LocalDate.of(2020, 03, 02);
        String formattedDate1 = date5.format(italianFormatter);
        System.out.println(formattedDate1);
        LocalDate date6 = LocalDate.parse(formattedDate1, italianFormatter);
        System.out.println(date6);

        System.out.println("===");
        DateTimeFormatter italianFormatter2 = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);
        LocalDate date7 = LocalDate.of(2020, 03, 02);
        String formattedDate2 = date7.format(italianFormatter2);
        System.out.println(formattedDate2);
    }
}
