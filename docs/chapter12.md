# 12. 새로운 날짜와 시간 API
 - 부실한 날짜와 시간 라이브러리 때문에 많은 개발자는 Joda-Time 같은 서드파티 날짜와 시간 라이브러리 사용.
   - 결국 자바 8에서 Joda-Time 의 많은 기능을 java.time 에 추가. 
## 12.1. LocalDate, LocalTime, Instant, Duration, Period 클래
### 12.1.1. LocalDate 와 LocalTime 사용
- LocalDate 인스턴스는 시간을 제외한 날짜를 표현하는 불변 객체.
   - 어떤 시간대 정보도 포함하지 않음
   - 정적 팩토리 메서드 of 로 LocalDate 인스턴스를 만들 수 있다.
~~~java
LocalDate date = LocalDate.of(2022, 03, 02);
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
~~~

- get 메서드에 TemporalField 를 전달해서 정보를 얻는 방법도 있다.
- 열거자 ChronoField 는 TemporalField 인터페이스를 정의하므로 ChronoField의 열거자 요소를 이용해서 원하는 정보를 쉽게 얻을 수 있다.

~~~java
 int year = date.get(ChronoField.YEAR);
  int month = date.get(ChronoField.MONTH_OF_YEAR);
  int day = date.get(ChronoField.DAY_OF_MONTH);
  System.out.println(year);
  System.out.println(month);
  System.out.println(day);
~~~

- LocalTime 클래스로 12:33:44 같은 시간을 표현할 수 있다.
- 위 처럼 게터 메서드  (getHour 등) 제공

- 문자열로 날짜 및 시간의 인스턴스를 만드는 방법도 있다. (parse 정적 메서드 사용)
~~~java
LocalDate parseDate = LocalDate.parse("2021-03-01");
System.out.println(parseDate);
LocalTime parseTime = LocalTime.parse("11:41:22");
System.out.println(parseTime);
~~~
- parse 메서드에 DateTimeFormatter 를 전달할 수도 있음.
   - 날짜, 시간 객체의 형식을 지정한다.
   - 기존 java.util.DateFormat 클래스를 대체하는 클래스

### 12.1.2. 날짜와 시간 조합
 - LocalDateTime 으로 날짜와 시간 모두 표현
~~~java
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

// toLocalDate, toLocalTime 로 날짜 or 시간 추출

LocalDate localDate = dt1.toLocalDate();
System.out.println(localDate);
LocalTime localTime = dt1.toLocalTime();
System.out.println(localTime);
~~~
### 12.1.3. Instant 클래스: 기계의 날짜와 시간
 - 기계의 관점에서는 연속된 시간에서 특정 지점을 하나의 큰 수로 표현하는 것이 가장 자연스러운 시간 표현 방법
 - java.time.Instant 클래스에서는 이와 같은 기계적인 관점에서 시간을 표현
 - Instant 클래스는 유닉스 에포크 시간 (1970년 1월 1일 0시 0분 0초 UTC) 을 기준으로 특정 지점까지의 시간을 초로 표현한다.
 - 팩토리 메서드 ofEpochSecond 에 초를 넘겨줘서 Instant 클래스 인스턴스를 만들 수 있다.
   - 정밀도 10억분의 1초(나노초)
   - 두번째 인수를 이용해서 나노초 단위로 시간도 보정 가능.

~~~java
   Instant instant = Instant.ofEpochSecond(3);
    System.out.println(instant);

    Instant instant1 = Instant.ofEpochSecond(3, 0);
    System.out.println(instant1);
    Instant instant2 = Instant.ofEpochSecond(2, 1_000_000_000);// 2초 이후의 1억 나노초
    System.out.println(instant2);
    Instant instant3 = Instant.ofEpochSecond(4, -1_000_000_000); // 4초 이전의 1억 나노초
    System.out.println(instant3);
~~~
결과

~~~java
1970-01-01T00:00:03Z
1970-01-01T00:00:03Z
1970-01-01T00:00:03Z
1970-01-01T00:00:03Z
~~~

- Instant 클래스도 사람이 확인할 수 있도록 시간을 표시해주는 팩터리 메서드 now 제공
  - 하지만 해당 클래스는 기계 전용의 유틸리티 이므로 사람이 읽을 수 있는 시간 정보를 제공하지 않는다. 

~~~java
  int day = Instant.now().get(ChronoField.DAY_OF_MONTH);

---
Exception in thread "main" java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: DayOfMonth
    at java.base/java.time.Instant.get(Instant.java:565)
    at io.jmlim.modernjavainaction.chap12.InstanctExample.main(InstanctExample.java:18)
~~~
### 12.1.4. Duration 과 Period 정의
 - Temporal 인터페이스는 특정 시간을 모델링하는 객체의 값을 어떻게 읽고 조작할지 정의한다.
   - 지금까지는 다양한 Temporal 인스턴스를 만드는 방법을 살펴봄
 - 이번에는 두 시간 객체 사이의 지속시간 duration을 만들어볼 차례.
   - Duration 클래스의 정적 팩토리 메서드 between 으로 두 시간 객체 사이의 지속시간을 만들 수 있다.
~~~java
LocalTime time1 = LocalTime.of(11, 22, 33);
LocalTime time2 = LocalTime.of(12, 22, 33);
Duration d1 = Duration.between(time1, time2);
System.out.println(d1);

LocalDateTime localDateTime1 = LocalDateTime.of(2020, 01, 01, 11, 22, 33);
LocalDateTime localDateTime2 = LocalDateTime.of(2020, 02, 01, 11, 22, 33);
Duration d2 = Duration.between(localDateTime1, localDateTime2);
System.out.println(d2);
~~~

결과
~~~
PT1H
PT744H1M1S
~~~
 - Duration 클래스는 초와 나노초로 시간 단위를 표현하므로 LocalDate 사용시엔 Period 클래스의 팩토리 메서드 between 을 사용한다.
~~~java
 Period tenDays = Period.between(LocalDate.of(2020, 01, 01)
        , LocalDate.of(2020, 01, 11));
System.out.println(tenDays);
~~~
결과
~~~java
P10D
~~~


|메서드|정적|설명|
|---|---|---|
|between|yes| 두 시간 사이의 간격을 생성함|
|from| yes| 시간 단위로 간격을 생성함 |
|of| yes| 주어진 구성 요소에서 간격 인스턴스를 생성함 |
|parse| yes| 문자열을 파싱해서 간격 인스턴스를 생성함 |
|addTo| no| 현재값의 복사본을 생성한 다음에 지정된 Temporal 객체에 추가함|
|get| no| 현재 간격 정보값을 읽음 |
|isNegative| no| 간격이 음수인지 확인함 |
|isZero |no| 간격이 0인지 확인함 |
|minus |no| 현재값에서 주어진 시간을 뺀 복사본을 생성함|
|multipliedBy| no| 현재값에 주어진 값을 곱한 복사본을 생성함|
|negated |no| 주어진 값의 부호를 반전한 복사본을 생성함|
|plus |no| 현재값에 주어진 시간을 더한 복사본을 생성함 |
|subtractFrom |no| 지정된 Temporal 객체에서 간격을 뺌|

## 12.2. 날짜 조정, 파싱, 포매팅
 - 절대적인 방식으로 LocalDate 의 속성 바꾸기 
~~~java
LocalDate date1 = LocalDate.of(2020, 01, 02);
LocalDate date2 = date1.withYear(2022);
System.out.println(date2);
LocalDate date3 = date2.withDayOfMonth(25);
System.out.println(date3);
LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 2);
System.out.println(date4);
~~~
결과
~~~java
2022-01-02
2022-01-25
2022-02-25
~~~
 - 선언형으로 LocalDate 를 사용하는 방법도 있음.
 - 상대적인 방식으로 LocalDate 속성 바꾸기
~~~java
LocalDate date5 = LocalDate.of(2020, 01, 02);
LocalDate date6 = date5.plusWeeks(1);
System.out.println(date6);
LocalDate date7 = date6.minusYears(6);
System.out.println(date7);
LocalDate date8 = date7.plus(6, ChronoUnit.MONTHS);
System.out.println(date8);
~~~
결과
~~~java
2020-01-09
2014-01-09
2014-07-09
~~~
|메서드|정적|설명|
|---|---|---|
|from |yes| 주어진 Temporal 객체를 이용해서 클래스의 인스턴스를 생성함|
|now |yes| 시스템 시계로 Temporal 객체를 생성함 |
|of |yes| 주어진 구성 요소에서 Temporal 객체의 인스턴스를 생성함 |
|parse |yes |문자열을 파싱해서 Temporal 객체를 생성함 |
|atOffset| no |시간대 오프셋과 Temporal 객체를 합침 |
|atZone |no |시간대 오프셋과 Temporal 객체를 합침 |
|format |no| 지정된 포매터를 이용해서 Tempral 객체를 문자열로 변환함 |
|get| no| Temporal 객체의 상태를 읽음 |
|minus |no |특정 시간을 뺀 Temporal 객체의 복사본을 생성함 |
|plus |no |현특정 시간을 더한 Temporal 객체의 복사본을 생성함 |
|with |no |일부 상태를 바꾼 Temporal 객체의 복사본을 생성함|

### 12.2.1. TemporalAdjusters 사용
 - 오버로드된 버전의 with 메서드에 좀 더 다양한 동작을 수행할 수 있도록 하는 기능을 제공하는 TemporalAdjuster 를 전달하는 방법으로 문제를 해결할 수 있다.
 - 날짜와 시간 API는 다양한 상황에서 사용할 수 있도록 다양한 TemporalAdjuster 를 제공한다.

~~~java
LocalDate date1 = LocalDate.of(2020, 3, 18);
LocalDate date2 = date1.with(nextOrSame(DayOfWeek.SUNDAY)); // 2020-03-23
System.out.println(date2);
LocalDate date3 = date2.with(lastDayOfMonth()); // 2020-03-31
System.out.println(date3);
~~~
결과
~~~
2020-03-22
2020-03-31
~~~

|메서드| 설명|
|---|---|
|dayOfWeekInMonth|서수 요일에 해당하는 날짜를 반환하는 TemporalAdjuster를 반환함|
|firstDayOfMonth|현재 달의 첫 번째 날짜를 반환|
|firstDayOfNextMonth|다음 달의 첫 번째 날짜를 반환|
|firstDayOfNextYear|내년의 첫 번째 날짜를 반환|
|firstInMonth|올해의 첫 번째 날짜를 반환|
|lastDayOfMonth|현재 달의 마지막 날짜를 반환|
|lastDayOfNextMonth|다음 달의 마지막 날짜를 반환|
|lastDayOfNextYear|내년의 마지막 날짜를 반환|
|lastDayOfYear|올해의 마지막 날짜를 반환|
|lastInMonth|현재 달의 마지막 요일에 해당하는 날짜를 반환|
|next previous|현재 달에서 현재 날짜 이후로 지정한 요일이 청므으로 나타나는 날짜를 반환하는 TemporalAdjuster를 반환함|
|nextOrSame|현재 날짜 이후로 지정한 요일이 처음으로 나타나는 날짜를 반환하는 TemporalAdjuster를 반환함|
|previousOrSame|현재 날짜 이후로 지정한 요일이 이전으로 나타나는 날짜를 반환하는 TemporalAdjuster를 반환함|

 - 위 예제에서 확인할 수 있는 것처럼 TemporalAdjuster 를 이용하면 좀 더 복잡한 날짜 조정 기능을 직관적으로 해결할 수 있다.
 - 그뿐이 아니라 필요한 기능이 정의되어 있지 않을 때는 비교적 쉽게 커스텀 TemporalAdjuster 구현을 만들 수 있다.

 - 메서드 하나만 정의 되어있음
~~~java
@FunctionalInterface
public interface TemporalAdjuster {
    Temporal adjustInto(Temporal temporal);
}
~~~

- 커스텀 TemporalAdjuster 구현하기
    - TemporalAdjuster 인터페이스를 구현하는 NwxtWorkingDay 를 구현하시오.
      - 이 클래스는 날짜를 하루씩 다음날로 바꾸는데 이 때 토요일과 일요일은 건너뛴다.
    - 사용예
~~~java
date = date.with(new NextWorkingDay());
~~~

~~~java
    quiz12_2();
...

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
~~~
결과
~~~java
2022-03-07T14:42:05
~~~

### 12.2.2. 날짜와 시간 객체 출력과 파싱
- DateTimeFormatter를 이용해서 날짜나 시간을 특정 형식의 문자열로 만들 수 있다.
- 기존의 java.util.DateTimeFormat 클래스와 달리 DateTimeFormatter 는 스레드에서 안전하게 사용할 수 있는 클래스다.
~~~java
LocalDate date = LocalDate.of(2020, 03, 02);
String s1 = date.format(DateTimeFormatter.BASIC_ISO_DATE);
String s2 = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
System.out.println(s1);
System.out.println(s2);
~~~
결과
~~~java
20200302
2020-03-02
~~~
 - parse 를 이용해서 문자열을 날짜 객체로 만들기
~~~java
LocalDate date1 = LocalDate.parse("20200302", DateTimeFormatter.BASIC_ISO_DATE);
LocalDate date2 = LocalDate.parse("2020-03-02", DateTimeFormatter.ISO_LOCAL_DATE);
System.out.println(date1);
System.out.println(date2);
~~~

 - 패턴으로 DateTimeFormatter 만들기
~~~java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate date3 = LocalDate.of(2020, 03, 02);
String formattedDate = date3.format(formatter);
System.out.println(formattedDate);
LocalDate date4 = LocalDate.parse(formattedDate, formatter);
System.out.println(date4);
~~~
결과
~~~java
02/03/2020
2020-03-02
~~~

- 지역화된 DateTimeFormatter 만들기
~~~java
DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
LocalDate date5 = LocalDate.of(2020, 03, 02);
String formattedDate1 = date5.format(italianFormatter);
System.out.println(formattedDate1);
LocalDate date6 = LocalDate.parse(formattedDate1, italianFormatter);
System.out.println(date6);
~~~
결과
~~~java
2. marzo 2020
2020-03-02
~~~
 
- DateTimeformatterBuilder 클래스로 복합적인 포메터를 정의해서 좀 더 세부적으로 포메터를 제어할 수 있다.
  - 대소문자, 관재한 규칙을 적용하는 파싱, 패딩 등
- 아래 예제는 아까 만든 italianFormatter 를 DateTimeFormatterBuilder를 통해 구성
~~~java
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
~~~
결과
~~~java
2. marzo 2020
~~~

## 12.3. 다양한 시간대와 캘린더 활용 방법
 - java.util.TimeZone 을 대테할 수 있는 java.time.ZoneId 가 새롭게 등장
 - 새로운 클래스를 이용하면 서머타임 같은 복잡한 사항이 자동으로 처리됨.

### 12.3.1. 시간대 활용하기
 - 표준 시간이 같은 지역을 묶어서 시간대(time zone) 규칙 집합을 정의한다.
 - ZoneRules 클래스에는 약 40개 정도의 시간대가 있다. ZoneId의 getRules() 를 이용해서 해당 시간대의 규정을 획득 할 수 있다.
 - 다음처럼 지역 아이디로 특정 ZoneId 를 구분한다.
~~~java
ZoneId romeZone = ZoneId.of("Europe/Rome")
~~~
 - 지역 아이디는 "지역/도시" 형식으로 이루어지며 IANA Time Zone Database 에서 제공하는 지역 집합 정보를 사용한다.
   - https://www.iana.org/time-zones 참고

 - 특정 시점대 시간대 적용 예제
~~~java
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
~~~
결과
~~~java
2020-03-02T00:00+01:00[Europe/Rome]
2020-03-18T13:45+01:00[Europe/Rome]
2022-03-02T07:34:40.181619100+01:00[Europe/Rome] // 당시 현재 시간이 한국시간 기준 15:34:40 초 였음. Rome 시간은 UTC 기준 +1 , 한국 시간은 +9 이므로 15-9+1 은 7시가 맞다.
~~~

Zoned 를 이용해서 LocalDateTime 을 Instant 로 바꾸는 방법도 있다.
~~~java
LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);
System.out.println(timeFromInstant);
~~~
결과
~~~java
2022-03-02T07:39:22.023960700 // 당시 현재 시간이 한국시간 기준 15:39:22 
~~~

### 12.3.2. UTC/Greenwich 기준의 고정 오프셋
 - 때로는 UTC/GMT 를 기준으로 시간대를 표현하기도 한다.
 - 예를 들어 뉴욕은 런던보다 5시간 느리다라고 표현할 수 있다.
 - ZoneId 의 서브클래스인 ZoneOffset 클래스로 런던의 그리니치 0도 자오선과 시간값의 차이를 표현할 수 있다.

~~~java
ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
~~~
- 위 예제에서 정의한 ZoneOffset 으로는 서머타임을 제대로 처리할 수 없으므로 권장하지 않는 방식.
- ISO-8601 캘린더 시스템에서 정의하는 UTC/GMT 와 오프셋으로 날짜와 시간을 표현하는 OffsetDateTime을 만드는 방법도 있다.

~~~java
ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
LocalDateTime dateTime = LocalDateTime.of(2020, Month.MARCH, 18, 13, 45);
OffsetDateTime dateTimeInNewWork = OffsetDateTime.of(dateTime, newYorkOffset);
System.out.println(dateTimeInNewWork);
~~~

결과
~~~java
2020-03-18T13:45-05:00
~~~

### 12.3.3. 대안 캘린더 시스템 사용하기


## 12.4. 마치며
 - 자바 8 이전 버전에서 제공하는 기존의 java.util.Date 클래스와 관련 클래스에서는 여러 불일치점들과 가변성, 어설픈 오프셋, 기본값, 잘못된 이름 결정 등의 설계 결함이 존재했다.
 - 새로운 날짜와 시간 API 에서 날짜와 시간 객체는 모두 불변이다.
 - 새로운 API는 각각 사람과 기계가 편리하게 날짜와 시간 정보를 관리할 수 있도록 두 가지 표현 방식을 제공한다.
 - 날짜와 시간 객체를 절대적인 방법과 상대적인 방법으로 처리할 수 있으며 기존 인스턴스를 변환하지 않도록 처리 결과로 새로운 인스턴스가 생성된다.
 - TemporalAdjuster 를 이용하면 단순히 값을 바꾸는 것 이상의 복잡한 동작을 수행할 수 있으며 자신만의 커스텀 날짜 변환 기능을 정의할 수 있다.
 - 날짜와 시간 객체를 특정 포멧으로 출력하고 파싱하는 포메터를 정의할 수 있다.
   - 패턴을 이용하거나 프로그램으로 포메터를 만들수 있으며 포메터는 스레드 안정성을 보장한다.
 - 특정 지역/장소에 상대적인 시간대 또는 UTC/GMT 기준의 오프셋을 이용해서 시간대를 정의할 수 있으며 이 시간대를 날짜와 시간 객체에 적용해서 지역화할 수 있다.
 - ISO-8601 표준 시스템을 준수하지 않는 캘린더 시스템도 사용할 수 있다.