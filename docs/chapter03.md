# 3. 람다 표현식
- 익명 클래스처럼 이름이 없는 함수면서 메서드를 인수로 전달할 수 있음
- 람다를 통해 더 간결하고 유연한 코드를 구현할 수 있음.

## 3.1. 람다란 무엇인가?
 - 특징
   - 익명 : 보통의 메서드와 달리 이름이 없으므로 익명이라 표현한다. 구현해야 할 코드에 대한 걱정거리가 줄어든다
   - 함수 : 다는 메서드처럼 특정 클래스에 종속되지 않으므로 함수라고 부른다. 하지만 메서드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트를 포함한다.
   - 전달 : 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
   - 간결성 : 익명 클래스처럼 많은 자질구레한 코드를 구현할 필요가 없다.

람다가 기술적으로 자바 8 이전의 자바로 할 수 없었던 일을 제공하는 것은 아니다. 다만 동작 파라미터를 이용할 때 익명 클래스 등 판에 박힌 코드를 구현할 필요가 없다. 
람다 표현식을 이용하면 2장에서 살펴본 동작 파라미터 형식의 코드를 더 쉽게 구현할 수 있다.

결과적으로 코드가 간결하고 유연해진다.

#### 람다의 구성
~~~
/* 람다 파라미터  | 화살표 |             람다 바디               */
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
~~~
 - 파라미터 리스트 - 람다 바디에서 사용할 "메서드" 파라미터를 명시한다.
   - Comparator의 compare 메서드 파라미터(사과 두 개)
 - 화살표 - 람다의 파라미터 리스트와 바디를 구분한다.
 - 람다 바디 - 람다의 반환값에 해당하는 표현식이다.

## 3.2. 어디에, 어떻게 람다를 사용할까? 
### 3.2.1. 함수형 인터페이스
 - 정확히 하나의 추상 메서드를 지정하는 인터페이스
 - 많은 디폴트 메서드가 있더라도 추상 메서드가 오직 하나면 함수형 인터페이스다
 - 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로 전체 표현식을 함수형 인터페이스의 인스턴스로 취급 할 수 있다.
 - 함수형 인터페이스에는 @FuntionalInterface 애노테이션을 함께 붙여주자. 
   - @FuntionalInterface는 함수형 인터페이스임을 가리키는 애노테이션이다. 
   - 만약 애노테이션을 선언했지만 실제로 함수형 인터페이스가 아니면 컴파일러가 에러를 발생시킨다.

### 3.2.2. 함수 디스크립터
 - 람다 표현식의 시그니처를 서술하는 메서드를 함수 디스크립터(function descripor) 라고 부른다.
 - 얘를 들어 Runnable 인터페이스의 유일한 추상 메서드 run 은 인수와 반환값이 없으므로(void 반환) Runnable 인터페이스는 인수와 반환값이 없는 시그니처로 생각할 수 있다.
 - 예) 함수형 인터페이스 Comparator의 compare 메서드의 함수 디스크립터는 (T, T) → int이다.

## 3.3. 람다 활용: 실행 어라운드 패턴
 - 자원처리(예를 들면 데이터베이스의 파일처리) 에 사용하는 순환 패턴은 자원을 결고, 처리한 다음에, 자원을 닫는 순서로 이루어진다.
 - 즉, 실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태를 갖는다. 위와 같은 형식의 코드를 실행 어라운드 패턴(execute around pattern) 이라고 부른다. 

~~~java
public String processFile() throws IOException {
    try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        return br.readLine() // 실제 필요한 작업을 하는 행    
    }
}
~~~

### 3.3.1. 1단계 : 동작 파라미터화를 기억하라
 - 기존의 설정, 정리 과정은 재사용하고 processFile 메서드만 다른 동작을 수행하도록 명령할 수 있다면 좋을 것이다.
   - 그렇다 processFile 의 동작을 파라미터화 하는 것이다.

ex) processFile 메서드가 한번에 두 행을 읽게 하려면 아래와 같이 고친다.
 - BufferedReader 를 인수로 받아서 String 을 반환하는 람다가 필요

~~~java
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
~~~

### 3.3.2. 2단계 : 함수형 인터페이스를 이용해서 동작 전달
 - 함수형 인터페이스 자리에 람다를 사용할 수 있다.
 - 따라서 BufferedReader -> String과 IOException 을 던질 수 있는 시그니처와 일치하는 함수형 인터페이스를 만들어야 한다.

~~~java
@FunctionalInterface
public interface BufferedReaderProcessor {
    String process(BufferedReader b) throws IOException;
}
~~~
- 정의한 인터페이스를 processFile 메서드의 인수로 전달할 수 있다.
~~~java
public String processFile(BufferedReaderProcessor p) throws IOException {
        ....    
}
~~~

### 3.3.3. 3단계 : 동작 실행
 - 이제 BufferedReaderProcessor 에 정의된 process 메서드의 시그니처(BufferedReader -> String)와 일치하는 람다를 전달할 수 있다.
 - 추상 메서드 구현을 직접 전달할 수 있으며 전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리.
   - process 를 구현하여 호출
~~~java
public String processFile(BufferedReaderProcessor p) throws IOException {
    try(BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
      return p.process(br); // 실제 필요한 작업을 하는 행    
    }
}
~~~

### 3.3.4. 4단계 : 람다 전달
 - 람다를 이용해서 다양한 동작을 processFile 메서드로 전달할 수 있다.
~~~java
// 한행 처리.
String oneLine = processFile((BufferedReader br) -> br.readLine());
~~~

~~~java
// 두행 처리 
String twoLines = processFile((BufferedReader br) -> br.readLine() + br.readLine());
~~~

> 다음 절에서는 다양한 람다를 전달하는 데 재활용할 수 있도록 자바 8에 추가된 새로운 인터페이스를 살펴본다.
 
## 3.4. 함수형 인터페이스 사용
 - 함수형 인터페이스의 추상 메서드 시그니처를 함수 디스크립터(function descriptor) 라고 한다. 
   - 다양한 람다 표현식을 사용하려면 공통의 함수 디스크립터를 기술하는 함수형 인터페이스 집합이 필요하다. 
   - 자바 8 라이브러리 설계자들은 java.util.function 패키지로 여러가지 함수형 인터페이스를 제공한다. 
   - 일단 대표적인 인터페이스들만 설명한다.
### 3.4.1. Predicate
   
~~~java
(T) → boolean

@FunctionalInterface
public interface Predicate<T> {
	boolean test(T t);
}
~~~

~~~java
public <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> results = new ArrayList<>();
    for(T t: list) {
        if(p.test(t)) {
            results.add(t);
        }    
    }
    return results;
}

...
Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
~~~

### 3.4.2. Consumer


~~~java
(T) → void

@FuncationalInterface
public interface Consumer<T> {
	void accept(T t);
}
~~~

~~~java
public <T> void forEach(List<T> list, Consumer<T> c) {
    for(T t: list) {
        c.accept(t);
    }    
}
forEach(
  Arrays.asList(1,2,3,4,5),
        (Integer i) -> System.out.println(i) // Consumer 의 accept 메서드를 구현하는 람다      
);
~~~
### 3.4.3. Function

~~~java
(T) → R

@FuncionalInterface
public interface Function<T, R> {
	R apply(T t);
}
~~~

~~~java
public <T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> result = new ArrayList<>();
    for(T t : list) {
        result.add(f.apply(t));  
    }
    return result;
}

// [7, 2, 6]
List<Integer> l = map(Arrays.asList("lamdbas", "in", "action"),
        (String s) -> s.length()  // <-- Function의 apply 메서드를 구현하는 람다.
);
~~~

#### 기본형 특화
제네릭은 참조형 타입만 지정할 수 있다.
Integer와 Long 같이 기본타입에 대하여 박싱된 타입을 통해 제네릭을 이용할 수 있지만, 오토박싱으로 인해 변환 비용이 소모된다. 

자바 8에 추가된 함수형 인터페이스는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 기본형에 특화된 버전의 함수형 인터페이스를 제공한다.

앞서보았던 함수형 인터페이스의 이름 앞에 사용하는 기본형 타입의 이름을 합친 이름으로 제공된다. 


예로 Predicate이나 파라미터로 int를 받는 Predicate을 처리할 수 있게 IntPredicate을 제공한다.
~~~java
@FunctionalInterface
public interface IntPredicate {
	boolean test(int t);
}
~~~
일반적으로 특정 형식을 입력으로 받는 함수형 인터페이스의 이름 앞에는 DoublePredicate, IntConsumer, LongBinaryOperator, IntFunction 처럼 형식명이 붙는다.
Function 인터페이스는 ToIntFunction<T>, IntToDoubleFunction 등의 다양한 출력 형식 파라미터를 제공한다.

필요하다면 우리가 직접 함수형 인터페이스를 만들 수 있다.

- 람다와 함수형 인터페이스 예제

|사용 사례|람다 예제|대응하는 함수형 인터페이스|
|---|---|---|
|불리언 표현|`(List<String> list) -> list.isEmpty()` | `Predicate<List<String>>` |
|객체 생성|`() -> new Apple(10)` |`Supplier<Apple>` |
|객체에서 소비|`(Apple a)-> System.out.println(a.getWeight())` |`Consumer<Apple>`|
|객체에서 선택/추출| `(String s) -> s.length()` |`Function<String, Integer> 또는 ToIntFunction<String>`|
|두 값 조합|`(int a, int b) -> a * b`|`IntBinaryOperator`|
|두 객체 비교|`(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())`|`Comparator<Apple> 또는 BiFunction<Apple, Apple, Integer> 또는 ToIntBiFunction<Apple, Apple>`|

#### 함수형 인터페이스와 예외
 - java.util.function의 함수형 인터페이스는 확인된 예외를 던지는 동작을 허용하지 않는다. 즉, 예외를 던지는 람다 표현식을 만드려면 확인된 예외를 선언하는 함수형 인터페이스를 직접 정의하거나 람다를 try/catch 블록으로 감싸야 한다.

## 3.5. 형식 검사, 형식 추론, 제약
### 3.5.1. 형식 검사
람다가 사용되는 콘텍스트(context)를 이용해서 람다의 형식(type)을 추론할 수 있다. 어떤 콘텍스트에서 기대되는 람다 표현식의 형식을 *대상 형식(target type)* 이라고 부른다. 형식검사는 다음과 같은 과정으로 진행된다.

예제) 
~~~java
List<Apple> heavierThan150g = filter(inventory, (Apple apple) -> apple.getWeight() > 150);
~~~

1. 람다가 사용된 메서드(filter 메서드)의 선언을 확인한다.
2. filter 메서드는 두번째 파라미터(Predicate<Apple>) 로 대상 형식을 기대한다.
3. 기대하는 파라미터의 함수형 인터페이스를 파악한다. (Predicate<Apple> 은 test 라는 한 개의 추상 메서드를 정의하는 함수형 인터페이스다.)
4. test 메서드는 Apple 을 받아 boolean 을 반환하는 함수 디스크립터를 묘사한다.
5. filter 메서드로 전달된 인수는 이와 같은 요구사항을 만족해야 한다.

위 예제에서 람다 표현식은 Apple 을 인수로 받아 boolean 을 반환하므로 유효한 코드다. 람다 표현식이 예외를 던달 수 있다면 추상 메서드도 같은 예외를 던질 수 있도록 throws 로 선언해야 한다.

### 3.5.2. 같은 람다, 다른 함수형 인터페이스
 - 대상 형식(target typing) 이라는 특징 때문에 같은 람다 표현식이더라도 호환되는 추상 메서드를 가진 다른 함수형 인터페이스로 사용될 수 있다.
 - 예를 들어 이전에 살펴본 Callable 과 PrivilegedAction 인터페이스는 인수를 받지 않고 제네릭 형식 T를 반환하는 함수를 정의한다.
   - 따라서 다음 두 할당문은 모두 유효한 코드다.
~~~java
Callable<Integer> c = () -> 42;
PrivilegedAction<Integer> p = () -> 42;
~~~

 - 하나의 람다 표현식을 다양한 함수형 인터페이스에 사용할 수 있다.
~~~java
Comparator<Apple> c1 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
ToIntBiFunction<Apple, Apple> c2 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
BiFunction<Apple, Apple, Integer> c3 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())
~~~

### 3.5.3. 형식 추론

제네릭을 사용할 때 선언부에 타입 매개변수를 명시하면 생성자에서는 빈 다이아몬드 연산자로 남겨두어도 자바 컴파일러는 생성 객체의 타입을 추론할 수 있다. 람다 표현식도 마찬가지이다. 자바 컴파일러는 람다 표현식이 사용된 콘텍스트를 이용해서 람다 표현식과 관련된 함수형 인터페이스를 추론한다.
~~~java
// 형식 추론을 하지 않음
Comparator<Apple> c =
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

// 형식을 추론함
Comparator<Apple> c =
(a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
~~~

### 3.5.4. 지역 변수 사용

람다 표현식에서는 익명 함수가 하는 것 처럼 자유 변수(파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수)를 활용할 수 있다. 이를 람다 캡처링(lamda capturing)이라 부른다. 하지만 그러려면 지역 변수는 명시적으로 final로 선언되어 있어야 하거나 실질적으로 final로 선언된 변수와 똑같이 사용되어야 한다(이후 재 할당 불가).

인스턴스 변수는 힙에 저장되는 반면 지역 변수는 스택에 위치한다. 람다에서 지역 변수에 바로 접근할 수 있다는 가정하에 람다가 스레드에서 실행된다면 지역 변수를 할당한 스레드가 사라져서 변수 할당이 해제되었는데도 람다를 실행하는 스레드에서는 해당 변수에 접근하려 할 수 있다. 따라서 자바 구현에서는 원래 변수에 접근을 허용하는 것이 아니라 자유 지역 변수의 복사본을 제공한다. 따라서 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 한 번만 값을 할당해야 한다는 제약이 생긴것이다.

## 3.6. 메서드 참조
### 3.6.1. 요약
 - 메서드 참조는 특정 메소드만을 호출하는 람다의 축약형이라고 생각할 수 있다.
예를 들어 람다가 '이 메서드를 직접 호출해' 라고 명령한다면 메서드를 어떻게 호출해야 하는지 설명을 참조하기보다는 메서드명을 직접 참조하는 것이 편리하다. 
 - 명시적으로 메서드명을 참조함으로써 가독성을 높일 수 있다.


 - 람다와 메서드 참조 단축 표현 예제

|람다 예제|메서드 참조 단축 표현|
|---|---|
|`(Apple apple) -> apple.getWeight()`|`Apple::getWeight`|
|`() -> Thread.currentThread().dumpStack()`|`Thread.currentThread()::dumpStack`|
|`(str, i) -> str.substring(i)`|`String::substring`|
|`(String s) -> System.out.println(s)`|`System.out::println`|
|`(String s) -> this.isValidName(s)`|`this::isValidName`|

#### 메서드 참조를 만드는 방법
1. 정적 메서드 참조 - Integer::parseInt
2. 다양한 형식의 인스턴스 메서드 참조 - String::length
3. 기존 객체의 인스턴스 메서드 참조 - 예를 들어 Transaction 객체를 할당받은 expensiveTransaction 지역 변수가 있고, Transaction 객체에는 getValue 메서드가 있다면, 이를 expensiveTransaction::getValue 라고 표현 할 수 있음.

### 3.6.2. 생성자 참조
 - ClassName::new 처럼 클래스명과 new 키워드를 이용해서 기존 생성자의 참조를 만들 수 있다.
 - 이는 정적 메서드의 참조를 만드는 방식과 비슷하다.

~~~java
Supplier<Apple> c1 = Apple::new; // () -> new Apple 과 같음
Apple a1 = c1.get(); // Supplier 의 get 메서드를 호출해서 새로운 Apple 객체를 만들 수 있다.
~~~

다음 코드에서 Integer 를 포함하는 리스트의 각 요소를 map 같은 메서드를 이용해서 Apple 생성자로 전달한다. 결과적으로 다양한 무게를 포함하는 사과 리스트가 만들어 진다.
~~~java
List<Integer> weights = Arrays.asList(7,3,4,10);
List<Apple> apples = map(weights, Apple::new);

        
... 

public List<Apple> map(List<Integer> list, Function<Integer, Apple> f) {
    List<Apple> result = new ArrayList<>();
    for(Integer i : list) {
        result.add(f.apply(i));   
     }
    return result;
}
~~~

## 3.7. 람다, 메서드 참조 활용하기
 - 처음에 다룬 사과 리스트를 다양한 정렬 기법으로 정렬하는 문제로 다시 돌아가서 이 문제를 더 세련되고 간결하게 해결하는 방법을 보여주면서 3장에서 배운 람다를 마무리한다.
### 3.7.1. 1단계 : 코드 전달
 - 자바 8의 List API에서 sort 메서드를 제공.

#### sort 메서드 시그니처
~~~java
void sort(Comparator<? super E> c);
~~~
 - 이 코드는 Comparator 객체를 인수로 받아 두 사과를 비교.
 - 객체 안에 동작을 포함시키는 방식으로 다양한 전략을 전달할 수 있다. 
   - sort 의 동작은 파라미터화 되었다라고 말할 수 있다.

~~~java

public class AppleComparator implements Comparator<Apple> {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
    }
}
...
inventory.sort(new AppleComparator());
~~~

### 3.7.2. 2단계 : 익명 클래스 사용
 - 한 번만 사용할 Comparator 를 위 코드처럼 구현하는 것보다는 익명클래스를 이용하는 것이 좋다.

~~~java
inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2) {
        return a1.getWeight().compareTo(a2.getWeight());
     } 
});
~~~
### 3.7.3. 3단계 : 람다 표현식 사용
 - 함수형 인터페이스를 기대하는 곳 어디에서나 람다 표현식을 사용할 수 있음을 배웠다.
 - Comparator 의 함수 디스크립터는 (T,T) -> int 다.

~~~java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
~~~
 - 파라미터 형식을 추론하면 다음처럼 더 줄일 수 있다.
~~~java
inventory.sort((a1, a2) -> a1.getWeight().compareTo(a2.getWeight()));
~~~
 - 가독성을 더 향상시키디. 
   - Comparator는 Comparable 키를 추출해서 Comparator 객체로 만드는 Function 함수를 인수로 받는 정적 메서드 comparing 을 포함한다. 
   - 다음처럼 comparing 메서드를 사용할 수 있다.

~~~java
Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());
~~~

 - 위 코드를 사용해서 간소화
~~~java
import static java.util.Comparator.comparing;
inventory.sort(comparing(apple -> apple.getWeight()));
~~~

### 3.7.4. 4단계 : 메서드 참조 사용
~~~java
inventory.sort(comparing(Apple::getWeight));
~~~
 - 코드 자체로 'Apple 을 weight 별로 비교해서 inventory 를 sort 하라' 는 의미로 전달 가능.

## 3.8. 람다 표현식을 조합할 수 있는 유용한 메서드
함수형 인터페이스에서는 다양한 유틸리티 메서드를 지원한다. Comparator, Function, Predicate 같은 함수형 인터페이스는 람다 표현식을 조합할 수 있도록 유틸리티 메서드를 제공하며, 간단한 여러 개의 람다 표현식을 조합해서 복잡한 람다 표현식을 만들수 있다. 이 유틸리티 메서드는 디폴트 메서드로 제공되어 함수형 인터페이스의 정의를 해치지 않으며 여러 조합을 가능케 하는 유틸리티를 제공한다.

### 3.8.1. Comparator 조합
 - comparing - 비교에 사용할 Function 기반의 키 지정
 - reversed - 역정렬
~~~java
inventory.sort(comparing(Apple::getWeight).reversed());
~~~
 - thenComparing - 동일한 조건에 대하여 추가적인 비교
~~~java
inventory.sort(comparing(Apple::getWeight)
        .reversed()
        .thenComparing(Apple::getCountry)); // 두 사과의 무게가 같으면 국가별로 정렬
~~~
### 3.8.2. Predicate 조합
 - and - and 연산
 - or - or 연산
 - negate - not 연산
~~~java
Predicate<Apple> notRedApple = redApple.negate(); // 기존 프레디케이트 객체 redApple 의 결과를 반전시킴.
~~~

### 3.8.3. Function 조합
 - andThen - 이후에 처리할 function 추가
 - compose - 이전에 처리되어야할 function 추가

## 3.9. 비슷한 수학적 개념
생략..
### 3.9.1. 적분
### 3.9.2. 자바 8 람다로 연결

## 3.10. 마치며
 - 람다 표현식은 익명함수의 일종이다. 이름은 없지만, 파라미터 리스트, 바디, 반환 형식을 가지며 예외를 던질 수 있다.
 - 람다 표현식으로 간결한 코드를 구현할 수 있다.
 - 함수형 인터페이스는 하나의 추상 메서드만을 정의하는 인터페이스이다. 
 - 함수형 인터페이스를 기대하는 곳에서만 람다 표현식을 사용할 수 있다.
 - 람다 표현식을 이용해서 함수형 인터페이스의 추상 메서드를 즉석으로 제공할 수 있으며 람다 표현식 전체가 함수형 인터페이스의 인스턴스로 취급된다. 
 - java.util.function 패키지로 다양한 함수형 인터페이스를 제공한다.
   - `Predicate<T>, Function<T,R>, Supplier<T>, Consumer<T>, BinaryOperator<T> 등`
 - 자바 8은 `Predicate<T>` 와 Function<T, R> 같은 제네릭 함수형 인터페이스와 관련한 박싱 동작을 피할 수 있는 IntPredicate, IntToLongFunction 과 같은 기본형 특화 인터페이스도 제공한다. 
 - 실행 어라운드 패턴(예를 들면 자원 할당, 자원 정리 등 코드 중간에 실행해야 하는 메서드에 꼭 필요한 코드)을 람다와 활용하면 유연성과 재사용성을 추가로 얻을 수 있다.
 - 람다 표현식의 기대 형식(type expected) 을 대상 형식(target type) 이라고 한다. 
 - 메서드 참조를 이용하면 기존의 메서드 구현을 재사용하고 직접 전달할 수 있다.
 - Comparator, Predicate, Function 과 같은 함수형 인터페이스는 람다 표현식을 조합할 수 있는 다양한 디폴트 메서드를 제공한다.

 > 책 이외 추가 참고 자료: https://github.com/ckddn9496/modern-java-in-action/blob/main/contents/Chapter%203%20-%20%EB%9E%8C%EB%8B%A4%20%ED%91%9C%ED%98%84%EC%8B%9D.md