# 5. 스트림 활용
스트림 API가 지원하는 다양한 연산을 살펴본다.
 - 필터링, 슬라이싱, 매핑, 검색, 매칭, 리듀싱 등 다양한 데이터 처리 질의 표현
 - 숫자 스트림, 파일과 배열 등 다양한 소스로 스트림을 만드는 방법과, 무한 스트림 등 스트림의 특수한 경우도 살펴폰다.

## 5.1. 필터링
### 5.1.1. 프레디케이트로 필터링
~~~java
Stream<T> filter(Predicate<? super T> predicate);
~~~

 - filter 메서드는 프레디케이트를 인수로 받아서 프레디케이트와 일치하는 모든 요소를 포함하는 스트림을 반환
 - 아래 코드에서 보여주는 것 처럼 모든 채식요리를 필터링해서 채식 메뉴를 만들 수 있다.
~~~java
List<Dish> vegetarianMenu = menu.stream()
        .filter(Dish::isVegetarian)
        .collect(toList());
~~~
### 5.1.2. 고유 요소 필터링

~~~java
Stream<T> distinct();
~~~
 - 고유 요소로 이루어진 스트림을 반환하는 distinct 메서드 지원.
   - 고유 여부는 스트림에서 만든 객체의 hashCode, equals 로 결정됨.

~~~java
// 모든 짝수를 선택하고 중복을 필터링
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream().filter(n -> n % 2 == 0)
        .distinct()
        .forEach(System.out::println);
~~~

## 5.2. 스트림 슬라이싱
- 스트림의 요소를 선택하거나 스킵하는 다양한 방법 설명
### 5.2.1. 프레디케이트를 이용한 슬라이싱

 - 요리 목록
~~~java
List<Dish> specialMenu = Arrays.asList(
    new Dish("season fruit", true, 120, Dish.Type.OTHER),
    new Dish("prawns", false, 300, Dish.Type.FISH),
    new Dish("rice", true, 350, Dish.Type.OTHER),
    new Dish("chicken", false, 400, Dish.Type.MEAT),
    new Dish("french fries", true, 530, Dish.Type.OTHER));
~~~

자바 9은 스트림의 요소를 효과적으로 선택할 수 있도록 takeWhile, dropWhile 두 가지 새로운 메소드 지원.

 - takeWhile - Predicate의 결과가 true인 요소에 대한 필터링. Predicate이 처음으로 거짓이 되는 지점에 연산을 멈춘다.
~~~java
Stream<T> takeWhile(Predicate<? super T> predicate)
~~~
- 리스트가 정렬되어 있다는 사실을 이용해 calories 가 320과 같거나 큰 요리가 나왔을 때 반복 작업을 중단.
~~~java
List<Dish> slicedMenu1 = specialMenu.stream()
        .takeWhile(dish -> dish.getCalories() < 320)
        .collect(toList());
~~~
- dropWhile - Predicate의 결과가 false인 요소에 대한 필터링. Predicate이 처음으로 거짓이 되는 지점까지 발견된 요소를 버린다.
~~~java
Stream<T> dropWhile(Predicate<? super T> predicate)
~~~

- dropWhile은 takeWhile 과 정 반대의 작업을 수행
    - 프레디케이트가 거짓이 되면 그 지점에서 작업을 중단하고 남은 모든 요소를 반환한다.
    - 무한한 남은 요소를 가진 무한 스트림에서도 동작한다.
~~~java
List<Dish> slicedMenu1 = specialMenu.stream()
        .dropWhile(dish -> dish.getCalories() < 320)
        .collect(toList());
~~~

### 5.2.2. 스트림 축소
 - limit - 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환한다.
~~~java
Stream<T> limit(long maxSize);
~~~

 - 300 칼로리 이상의 세 요리를 선택해서 리스트 만들기
~~~java
List<Dish> dishes = specialMenu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .limit(3)
        .collect(toList());
~~~
### 5.2.3. 요소 건너뛰기
 - skip - 처음 n개 요소를 제외한 스트림을 반환한다.
   - n개 이하의 요소를 포함하는 스트림에 skip(n) 호출하면 빈 스트림이 반환된다.

~~~java
Stream<T> skip(long n);
~~~

 - 300 칼로리 이상의 처름 두 요리를 건너뛴 다음 300 칼로리가 넘는 나머지 요리를 반환
~~~java
List<Dish> dishes = menu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .skip(2)
        .collect(toList());
~~~

## 5.3. 매핑
### 5.3.1. 스트림의 각 요소에 함수 적용하기
 - 스트림은 함수를 인수로 받는 map 메서드를 지원. 인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다.
   - 이 과정은 기존의 값을 고친다라는 개념보다는 새로운 버전을 만든다라는 개념에 가까우므로 변환에 가까운 매핑이라는 단어를 사용한다.
 - map - 함수를 인수로 받아 새로운 요소로 매핑된 스트림을 반환한다. 기본형 요소에 대한 mapToType 메서드도 지원한다 (mapToInt, mapToLong, mapToDouble).

~~~java
<R> Stream<R> map(Function<? super T, ? extends R> mapper);
~~~

- 각 요리명의 길이 반환
~~~java
List<Integer> dishNameLengths = menu.stream()
        .map(Dish::getName)
        .map(String::length)
        .collect(toList());
~~~

### 5.3.2. 스트림 평면화
 - flatMap - 제공된 함수를 각 요소에 적용하여 새로운 하나의 스트림으로 매핑한다. 결과적으로 하나의 평면화된 스트림을 반환한다.
~~~java
<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);
~~~
- ["Hello", "World"] 리스트를 ["H","e","l","o","W","r","d"] 로 변환
~~~

List<String> word = Arrays.asList("Hello","World");
List<String> heloWrd = word
    .stream().map(s -> s.split("")) // 각 단어를 개별 문자열 배열로 변환 Stream<String[]>
    .flatMap(Arrays::stream) // 생성된 스트림을 하나의 스트림으로 평면화 Stream<String>
    .distinct() // Stream<String>
    .collect(toList()); // List<String>
~~~

## 5.4. 검색과 매칭
 - 특정 속성이 데이터 집합에 있는지 여부를 검색하는 데이터 처리도 자주 사용된다. 
 - 스트림 API는 allMatch, anyMatch, noneMatch, findFirst, findAny 등 다양한 유틸리티 메서드를 제공한다.
### 5.4.1. 프레디케이트가 적어도 한 요소와 일치하는지 확인
 
~~~java
boolean anyMatch(Predicate<? super T> predicate);
~~~
  - 프레디케이트가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때 anyMatch 메서드를 이용한다.
    - 다음 코드는 menu에 채식요리가 있는지 확인하는 예제
~~~java
if(menu.stream().anyMatch(Dish::isVegetarian)) {
    System.out.println("The menu is (somewhat) vegetarian friendly !!");    
}
~~~

### 5.4.2. 프레디케이트가 모든 요소와 일치하는지 검사
~~~java
boolean allMatch(Predicate<? super T> predicate);
~~~
 - allMatch 메서드는 anyMatch 와 달리 스트림의 모든 요소가 주어진 프레디케이트와 일치하는지 검사
   - ex) 메뉴가 건강식(모든 요리가 1000 칼로리 이하면 건강식으로 간주)인지 확인할 수 있다.
~~~java
boolean isHealthy = menu.stream().allMatch(dish -> dish.getCalories() < 1000);
~~~

~~~java
boolean noneMatch(Predicate<? super T> predicate);
~~~
 - noneMatch는 allMatch 와 반대 연산을 수행. 모든 요소가 일치하지 않는지 검사하는 최종 연산이다. 
   - 이전 예제를 noneMatch 로 아래와 같이 구현 가능.
~~~java
boolean isHealthy = menu.stream().noneMatch(dish -> dish.getCalories() >= 1000);
~~~
> anyMatch, allMatch, noneMatch 세 메서드는 스트림 쇼트서킷 기법, 즉 자바의 && , || 와 같은 연산을 활용한다. 

> 때로는 전체 스트림을 처리하지 않았더라도 결과를 반환할 수 있다. 
> 예를 들어 여러 and 연산으로 연결된 커다란 불리언 표현식을 평가한다고 가정하자. 
> 표현식에서 하나라도 거짓이라는 결과가 나오면 나머지 표현식의 결과와 상관없이 전체 결과도 거짓이 된다. 
> 이러한 상황을 쇼트 서킷 이라고 부른다. allMatch, noneMatch, findFirst, findAny 등의 연산은 모든 스트림의 요소를 처리하지 않고도 반환할 수 있다.
> limit 도 쇼트서킷 연산이다. 특히 무한한 요소를 가진 스트림을 유한한 크기로 줄일 수 있는 유용한 연산이다. 

### 5.4.3. 요소 검색
 - findAny 메서드는 현재 스트림에서 임의의 요소를 반환한다. findAny 메서드를 다른 스트림 연산과 연결해서 사용할 수 있다. 
 - 요소를 찾르면 바로 반환, 요소의 반환순서가 상관없을 때 findFirst 대신 사용한다.
~~~java
Optional<T> findAny();
~~~
 - filter 와 findAny 이용해서 채식 요리 선택
~~~java
Optional<Dish> dish = menu.stream()
        .filter(Dish::isVegetarian)
        .findAny();// 쇼트서킷을 이용해서 결과를 찾는 즉시 실행을 종료한다.
~~~

#### Optional 이란?
- 값의 존재나 부재 여부를 표현하는 컨테이너 클래스.
  - findAny는 아무 요소도 반환하지 않을 수 있다. null은 쉽게 에러를 일으킬 수 있으므로 자바 8 라이브러리 설계자는 Optional<T> 를 만들었다.
  - Optional 은 값이 존재하는지 확인하고 값이 없을 때 어떻게 처리할지 강제하는 기능을 제공한다.

- isPresent() 는 Optional 이 값을 포함하면 true, 값을 포함하지 않으면 false 반환
- ifPresent(Consumer<T> block) 은 값이 있으면 주어진 블록을 실행한다. 
  - 없으면 아무일도 일어나지 않음.
- T get() 은 값이 존재하면 값을 반환하고, 값이 없으면 NoSuchElementException을 일으킨다. 
- T orElse(T other) 는 값이 있으면 값을 반환하고, 값이 없으면 기본값을 반환한다.


### 5.4.4. 첫 번째 요소 찾기
~~~java
Optional<T> findFirst();
~~~

> findFirst() 메서드는 Stream 에서 첫 번째 요소를 찾아서 Optional 타입으로 리턴합니다. 조건에 일치하는 요소가 없다면 empty 가 리턴됩니다. 따라서 Stream 의 첫 번째 요소를 구체적으로 원할 때 이 방법을 사용합니다.
> findFirst() 메서드의 동작은 병렬 처리에서도 동일합니다.

> findFirst() 와 findAny() 의 가장 큰 차이는 병렬로 처리할 때 발생합니다. 병렬 처리를 진행하면, findAny() 는 멀티스레드가 Stream 을 처리할 때 가장 먼저 찾은 요소를 리턴하여, 실행할 때마다 리턴값이 달라집니다. 
> 최대 성능을 내기 위해 병렬 작업을 처리하는 경우 리턴값을 안정적으로 반환할 수 없습니다.

## 5.5. 리듀싱
 - 리듀스 연산을 이용해서 '메뉴의 모든 칼로리의 합계를 구하시오', '메뉴에서 칼로리가 가장 높은 요리는?' 같이 스트림 요소를 조합해서 더 복잡한 질의를 표현하는 방법을 설명한다.
 - 이러한 질의를 수행하려면 Integer 같은 결과가 나올 때까지 스트림의 모든 요소를 반복적으로 처리해야 한다. 
   - 이런 질의를 리듀싱 연산 이라고 한다.

 - reduce - 모든 스트림 요소를 BinaryOperator로 처리해서 값으로 도출한다. 두 번째 reduce 메서드와 같은 경우 초기값(identity)가 없으므로 아무 요소가 없을 때를 위해 Optional<T>를 반환한다.
~~~java
T reduce(T identity, BinaryOperator<T> accumulator);
Optional<T> reduce(BinaryOperator<T> accumulator);
<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);
~~~

### 5.5.1. 요소의 합

 - for-each 루프를 이용해서 리스트의 숫자 요소를 더하는 코드
~~~java
int sum = 0;
for(int x : numbers) {
    sum += x;    
}
~~~

 - reduce 를 이용해서 스트림의 모든 요소를 더하기.
~~~java
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
~~~
리듀스는 두개의 인수를 갖는다. 
 - 초기값 0
 - 두 요소를 조합해서 새로운 값을 만드는 BinaryOperator<T>, 예제에서는 람다 표현식 (a, b) -> a + b 사용
 

- 메서드 참조를 이용해서 코드를 더 간결하게 만들기. 
  - 자바 8에서는 Integer 클래스에 두 숫자를 더하는 정적 sum 메서드 제공
~~~java
int sum = numbers.stream().reduce(0, Integer::sum);
~~~

#### 초기값 없음
 - 초기값을 받지 않도록 오버로드된 reduce 도 있다.
 - 이 reduce 는 Optional 객체를 반환한다. 
   - 스트림에 아무 요소도 없는 상황을 생각해보면 reduce 가 합계를 반환할 수 없으므로 합계가 없음을 가리킬 수 있도록 Optional 객체로 감싼 결과를 반환한다.
~~~java
Optional<Integer> sum = numbers.stream().reduce(Integer::sum);
~~~

### 5.5.2. 최대값과 최소값
 - 최대값과 최솟값을 찾을 때도 reduce 를 활용할 수 있다. 
 - reduce 는 두 인수를 받는다.
   - 초깃값
   - 스트림의 두 요소를 합쳐서 하나의 값으로 만드는 데 사용할 람다. 

 - 두 요소중에 최대 또는 최소값을 찾는 메서드
   - max, min - 요소에서 최댓값과 최솟값을 반환한다. 마찬가지로, 빈 스트림일 수 있기에 Optional<T> 를 반환한다.
~~~java
Optional<T> max(Comparator<? super T> comparator);
Optional<T> min(Comparator<? super T> comparator);
~~~

~~~java
// 최대값 찾기
Optional<Integer> max = numbers.stream().reduce(Integer::max);
// 최소값 찾기
Optional<Integer> min = numbers.stream().reduce(Integer::min);

// Integer::min 대신 (x, y) -> x < y ? x : y 를 사용해도 무방하지만 메서드 참조 표현이 더 읽기 쉽다.
~~~

#### reduce 메서드의 장점과 병렬화
 - reduce를 이용하면 내부 반복이 추상화되면서 내부 구현에서 병렬로 reduce를 실행할 수 있게 된다. 
 - 반복적인 합계에서는 sum 변수를 공유해야 하므로 쉽게 병렬화하기 어렵다.
 - 스트림은 내부적으로 포크/조인 프레임워크(fork/join framework)를 통해 이를 처리한다.

~~~java
int sum = numbers.parallelStream().reduce(0, Integer::sum);
// 위 코드를 병렬로 실행하려면 대가를 지불해야 한다. 
// 즉 reduce 에 넘겨준 람다의 상태(인스턴스 변수 같은) 가 바뀌지 말아야 하며, 연산이 어떤 순서로 실행되더라도 결과가 바뀌지 않는 구조여야 한다.
~~~

#### 스트림 연산 : 상태 없음과 상태 있음
 - map, filter 등은 입력 스트림에서 각 요소를 받아 0 또는 결과를 출력 스트림으로 보낸다.
    - 따라서 이들은 보통 상태가 없는, 내부 상태를 갖지 않는 연산(stateless operation) 이다. 
 - 하지만 reduce, sum, max 같은 연산은 결과를 누적할 내부 상태가 필요하다. 
   - 예제의 내부 상태는 작은 값이다. (우리 예제에서는 int 또는 double 을 내부 상태로 사용) 
     - 스트림에서 처리하는 요소 수와 관계없이 내부 상태의 크기는 한정(bounded) 되어 있다. 
 - 반면 sorted 나 distinct 같은 연산은 스트림의 요소를 정렬하거나 중복을 제거하기 위해 과거의 이력을 알고 있어야 한다. 
   - 예를 들어 어떤 요소를 출력 스트림으로 추가하려면 모든 요소가 버퍼에 추가되어 있어야 한다.
   - 따라서 데이터 스트림의 크기가 크거나 무한이라면 문제가 생길 수 있다. 이러한 연산을 내부 상태를 갖는 연산(stateful operation)이라 한다.

## 5.6. 실전 연습
(chap05 패키지에 코드 작성)
1. 2011 년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리하시오.
2. 거래자가 근무하는 모든 도시를 중복없이 나열하시오.
3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.
4. 모든 거래자의 이름을 알파벳순으로 정렬해서 반환하시오
5. 밀라노에 거래자가 있는가?
6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오.
7. 전체 트랜잭션 중 최댓값은 얼마인가?
8. 전체 트랜잭션 중 최솟값은 얼마인가?

### 5.6.1. 거래자와 트랜잭션
### 5.6.2. 실전 연습 정답

## 5.7. 숫자형 스트림
- 스트림 API는 숫자 스트림을 효율적으로 처리할 수 있도록 기본형 특화 스트림(primitive stream specialization)을 제공한다

### 5.7.1. 기본형 특화 스트림
 - 기본형 특화 스트림으로 IntStream, DoubleStream, LongStream이 존재하며 각각의 인터페이스에는 숫자 스트림의 합계를 계산하는 sum, 최댓값 요소를 검색하는 max 같이 자주 사용하는 숫자 관련 리듀싱 연산 메서드를 제공한다.

#### 숫자 스트림으로 매핑
 - 스트림을 특화 스트림으로 변환할 때는 mapToInt, mapToDouble, mapToLong 세 가지 메서드를 가장 많이 사용한다. 
 - 이들 메서드는 map 과 정확히 같은 기능을 수행하지만, `Stream<T>` 대신 특화된 스트림을 반환한다.
~~~java
int calories = menu.stream() // Stream<Dish> 반환
        .mapToInt(Dish::getCalories) // IntStream 반환
        .sum(); // IntStream 은 max, min, average 등 다양한 유틸리티 메서드도 지원
~~~

#### 객체 스트림으로 복원하기.
 - boxed 메서드를 이용하면 특화 스트림을 일반 스트림으로 변환할 수 있다.

~~~java
Stream<Integer> boxed(); // in IntStream
~~~

#### 기본값 : OptionalInt
 - 합계 예제에서는 0이라는 기본값이 있었으므로 별 문제가 없었다. 하지만 IntStream 에서 최댓값을 찾을 때는 0이라는 기본값 때문에 잘못된 결과가 도출 될 수 있다. 
 - 스트림에 요소가 없는 상황과 실제 최댓값이 0인 상황을 어떻게 구별할 수 있을까?
   - Optional을 Integer, String등의 참조 형식으로 파라미터화 할 수 있다.
   - OptionalInt, OptionalDouble, OptionalLong 세 가지 기본형 특화 스트림 버전을 제공한다.

~~~java
OptionalInt maxCalories = menu.stream().mapToInt(Dish::getCalories);
maxCalories.orElse(1); // 값이 없을 때 기본 최댓값을 명시적으로 설정.
~~~

### 5.7.2. 숫자 범위
 - IntStream, LongStream 두 기본형 특화 스트림에서 range, rangeClosed 메서드를 지원
 - range 메서드는 시작값과 종료값이 결과에 포함되지 않는 반면 rangeClosed 는 시작값과 종료값이 결과에 포함된다는 점이 다르다.

~~~java
IntStream evenNumbers = IntStream.rangeClosed(1, 100) // [1,100] 범위를 나타냄, range를 사용하면 1, 100 은 포함하지 않음.
        .filter(n -> n % 2 == 0); // 1부터 100 까지의 짝수 스트림 반환
System.out.println(evenNumbers.count()); // 1부터 100까지에는 50개의 짝수가 있다.
~~~
### 5.7.3. 숫자 스트림 활용 : 피타고라스의 수
 - 지금까지 배운 슷자 스트림과 스트림 연산을 더 활용할 수 있는 어려운 예제를 살펴보자.

#### 피타고라스의 수
> 공식: a * a + b * b = c * c
> 
> 3,4,5 를 공식에 대입 : `3*3 + 4*4 = 5*5` , `9 + 16 = 25`

#### 세 수 표현하기.
우선 세 수를 정의해야 한다. 세 요소를 갖는 int 배열을 사용하는 것이 좋을 것 같다.
ex : new int[] {3, 4, 5}

#### 좋은 필터링 조합
누군가 세 수 중에서 a,b 두 수만 제공했다고 가정하자. 두 수가 피터고라스 수의 일부가 될 수 있는 좋은 조합인지 어떻게 확인 가능?
a * a + b * b 의 제곱근이 정수인지 확인할 수 있다. 

~~~java
Math.sqrt(a*a + b*b) % 1 == 0;

// 이때 x 가 부동 소숫점 수라면 x % 1.0 이라는 자바 코드로 소숫점 이하 부분을 얻을 수 있다.
// ex) 5.0 이라는 수에 이 코드를 적용하면 소숫점 이하는 0이 됨.
~~~

~~~java
filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
~~~

#### 집합 생성
~~~java
stream().filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
        .map(b -> new int[] {a, b, (int) Math.sqrt(a * a + b * b)});
~~~

#### b값 생성
~~~java
IntStream.rangedClosed(1, 100)
        .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
        .boxed()
        .map(b -> new int[] {a, b, (int) Math.sqrt(a * a + b * b)});
~~~

IntStream 의 mapToObj 메서드를 이용해서 재 구현
~~~java
IntStream.rangedClosed(1, 100)
        .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
        .mapToObj(b -> new int[] {a, b, (int) Math.sqrt(a * a + b * b)});
~~~

#### a 값 생성
최종 완성 코드
~~~java
Stream<int[]> pythagoreanTriples = IntStream.rangedClosed(1, 100)
        .boxed()
        .flatMap(a -> 
            IntStream.rangedClosed(1, 100)
                        .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
                        .mapToObj(b -> new int[] {a, b, (int) Math.sqrt(a * a + b * b)})
        );
~~~

#### 코드 실행
~~~java
pythagoreanTriples.limit(5)
        .forEach(t -> System.out.println(t[0] + " ," +t[1] + " ," +t[2] ));
~~~
~~~
// 실행결과
3, 4, 5
5, 12, 13
6, 8, 10
7, 24, 25
8, 15, 17
~~~

## 5.8. 스트림 만들기
### 5.8.1. 값으로 스트림 만들기
 - 정적 메서드 Stream.of 을 이용하여 스트림을 만들 수 있다.
~~~java
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action")
~~~
 - empty 메서드를 이용해서 스트림을 비울 수도 있다. 
~~~java
Stream<String> emptyStream = Stream.empty();
~~~
### 5.8.2. null 이 될 수 있는 객체로 스트림 만들기
- 자바 9부터 지원되며 Stream.ofNullable 메서드를 이용하여 null이 될 수 있는 객체를 지원하는 스트림을 만들 수 있다.
~~~java
Stream<String> homeValueStream = Stream.ofNullable(System.getProperty("home"));
~~~
 - null 이 될 수 있는 객체를 포함하는 스트림값을 flatMap과 함께 사용하는 상황에서는 이 패턴을 더 유용하게 사용할 수 있다.
~~~java
Stream<String> values = Stream.of("config", "home", "user")
        .flatMap(key -> Stream.ofNullable(System.getProperty(key)));
~~~

### 5.8.3. 배열로 스트림 만들기
 - 배열을 인수로 받는 정적 메서드 Arrays.stream 을 이용하여 스트림을 만들 수 있다.

~~~java
int[] numbers = [2,3,5,7,11,13];
int sum = Arrays.stream(numbers).sum();
~~~
### 5.8.4. 파일로 스트림 만들기
 - 파일을 처리하는 등의 I/O 연산에 사용하는 자바의 NIO API(비블록 I/O)도 스트림 API를 활용할 수 있도록 업데이트 되었다. 
 - java.nio.file.Files의 많은 정적 메서드가 스트림을 반환한다. 
   - 예를 들어 Files.lines는 주어진 파일의 행 스트림을 문자열로 반환한다.

### 5.8.5. 함수로 무한 스트림 만들기
- Stream.iterate와 Stream.generate를 통해 함수를 이용하여 무한 스트림을 만들 수 있다. 
iterate와 generate에서 만든 스트림은 요청할 때마다 주어진 함수를 이용해서 값을 만든다. 
따라서 무제한으로 값을 계산할 수 있지만, 보통 무한한 값을 출력하지 않도록 limit(n) 함수를 함께 연결해서 사용한다.

#### iterate 메서드
- Stream.iterate
~~~java
public static<T> Stream<T> iterate(final T seed, final UnaryOperator<T> f)
~~~

~~~java
Stream.iterate(0, n -> n + 2) 
        .limit(10)
        .forEach(System.out::println);
~~~

자바 9의 iterate 메서드는 프레디케이트를 지원한다. 예를 들어 0에서 시작해서 100 보다 크다면 숫자 생성을 중단하는 코드를 다음처럼 구현할 수 있다.

~~~java
IntStream.iterate(0, n -> n < 100, n -> n + 4)
        .forEach(System.out::println)
~~~

 - Stream.generate
   - iterate 와 달리 generate 는 생산된 각 값을 연속적으로 계산하지 않는다. 
   - generate는 Supplier<T> 를 인수로 받아서 새로운 값을 생산한다. 
~~~java
public static<T> Stream<T> generate(Supplier<T> s)
~~~

~~~java
// random stream of doubles with Stream.generate
Stream.generate(Math::random)
        .limit(10)
        .forEach(System.out::println);

~~~
~~~java
// stream of 1s with Stream.generate
IntStream.generate(() -> 1)
.limit(5)
.forEach(System.out::println);
~~~

- 위에 우리가 사용한 발행자(supplier)는 상태가 없는 메서드, 즉 나중에 계산에 사용할 어떤 값도 저장해두지 않는다.
- 여기서 중요한 점은 병렬 코드에서는 발행자에 상태가 있으면 안전하지 않다는 것이다. 
  - 따라서 상태를 갖는 발행자는 단지 설명에 필요한 예제일 뿐 실제로는 피해야 한다. 
  - generate를 이용해서 피보나치 수열을 구현해보면서 iterate메서드와 generate를 비교할 수 있다.

- 익명클래스와 람다는 비슷한 연산을 수행하지만 익명 클래스에서는 getAsInt 메서드의 연산을 커스터마이즈 할 수 있는 상태 필드를 정의할 수 있다는 점이 다르다.
  - 바로 부작용이 생길 수 있음을 보여주는 예제다.

~~~java
IntSupplier fib = new IntSupplier() {
    
    private int previous = 0;
    private int current = 1;

    @Override
    public int getAsInt() {
        int nextValue = previous + current;
        previous = current;
        current = nextValue;
        return previous;
    }
};
~~~
~~~java
IntStream.generate(fib)
    .limit(10)
    .forEach(System.out::println);
~~~
 - 위 코드에서는 IntSupplier 인스턴스를 만들었다. 만들어진 객체는 기존 피보나치 요소와 두 인스턴스 변수에 어떤 피보나치 요소가 들어있는지 추적하므로 가변(mutable)상태 객체다.
 - getAsInt를 호출하면 객체 상태가 바뀌며 새로운 값을 생산한다. 
 - iterate를 사용했을 때는 각 과정에서 새로운 값을 생성하면서도 기존 상태를 바꾸지 않는 순수한 불변(immutable) 상태를 유지 했다.
 - 스트림을 병렬로 처리하면서 올바른 결과를 얻으려면 불변 상태 기법을 고수해야 한다.

## 5.9. 마치며
- 스트림 API를 이용하면 복잡한 데이터 처리 질의를 표현할 수 있다.
- filter, distinct, takeWhile(자바9), dropWhile(자바9), skip, limit 메서드로 스트림을 필터링하거나 자를 수 있다.
- 소스가 정렬되어 있다는 사실을 알고 있을 때 takeWhile 과 dropWhile 메소드를 효과적으로 사용할 수 있다.
- map, flatMap 메서드로 스트림의 요소를 추출하거나 변환할 수 있다.
- findFirst, findAny 메서드로 스트림의 요소를 검색할 수 있다.
  - allMatch, noneMatch, anyMatch 메서드를 이용해서 주어진 프레디케이트와 일치하는 요소를 스트림에서 검색 할 수 있다.
- 이들 메서드는 쇼트서킷, 즉 결과를 찾는 즉시 반환하며, 전체 스트림을 처리하지 않는다.
- reduce 메서드로 스트림의 모든 요소를 반복 조합하여 값을 도출할 수 있다. 예를 들어 reduce 로 스트림의 최댓값이나 모든 요소의 합계를 계산할 수 있다.
- filter, map 등은 상태를 저장하지 않는 상태 없는 연산(stateless operation) 이다. reduce 같은 연산은 값을 계산하는 데 필요한 상태를 저장한다. sorted, distinct 등의 메서드는 새로운 스트림을 반환하기에 앞서 스트림의 모든 요소를 버퍼에 저장해야 한다. 이런 메서드를 상태 있는 연산(stateful operation) 이라고 부른다. 
- IntStream, DoubleStream, LongStream 은 기본형 특화 스트림이다. 이들 연산은 각각의 기본형에 맞게 특화되어 있다.
- 컬렉션뿐 아니라 값, 배열, 파일, iterate와 generate 같은 메서드로도 스트림을 만들 수 있다.
- 무한한 개수의 요소를 가진 스트림을 무한 스트림이라 한다.


추가 참고자료 
 - https://github.com/ckddn9496/modern-java-in-action/blob/main/contents/Chapter%205%20-%20%EC%8A%A4%ED%8A%B8%EB%A6%BC%20%ED%99%9C%EC%9A%A9.md
 - https://has3ong.github.io/programming/java-streamintro3/
