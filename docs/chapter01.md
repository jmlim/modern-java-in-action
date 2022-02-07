- [1. 자바 8,9,10,11 : 무슨일이 일어나고 있는가?](#1----8-9-10-11-----------------)
    * [1.1. 역사의 흐름은 무엇인가](#11-------------)
    * [1.2. 왜 아직도 자바는 변화하는가?](#12-----------------)
        + [1.2.1. 프로그래밍 언어 생태계에서 자바의 위치](#121----------------------)
        + [1.2.2. 스트림 처리](#122-------)
        + [1.2.3. 동작 파라미터화로 메서드에 코드 전달하기](#123-----------------------)
        + [1.2.4. 병렬성과 공유 가변 데이터](#124---------------)
        + [1.2.5. 자바가 진화해야 하는 이유](#125---------------)
    * [1.3. 자바 함수](#13------)
        + [1.3.1. 메서드와 람다를 일급 시민으로](#131-----------------)
        + [1.3.2. 코드 넘겨주기 : 예제](#132-------------)
        + [1.3.3. 메서드 전달에서 람다로](#133-------------)
    * [1.4. 스트림](#14----)
        + [1.4.1. 멀티스레딩은 어렵다.](#141-----------)
    * [1.5. 디폴트 메서드와 자바 모듈](#15---------------)
    * [1.6. 함수형 프로그래밍에서 가져온 다른 유용한 아이디어](#16----------------------------)
    * [1.7. 마치며](#17----)
    
# 1. 자바 8,9,10,11 : 무슨일이 일어나고 있는가?
---


---
## 1.1. 역사의 흐름은 무엇인가

자바 역사를 통틀어 가장 큰 변화가 자바 8에서 일어남.. 자바 9에서도 중요한 변화가 있었지만 자바 8만큼 획기적이거나 생산성이 바뀌는 것은 아니었다. 
자바 10에서는 형 추론과 관련해 약간의 변화만 일어났다.

자바 8은 간결한 코드, 멀티코어 프로세서의 쉬운 활용이라는 두 가지 요구사항을 기반으로 한다.

일단 자바 8에서 제공하는 새로운 기술이 어떤 것인지 확인하자.
- 스트림 API
- 메서드에 코드를 전달하는 기번
- 인터페이스의 디폴트 메서드.

자바 8은 데이터베이스의 질의 언어에서 표현식을 처리하는 것처럼 병렬 연산을 지원하는 스트림이라는 새로운 API를 제공한다.
- 데이터베이스 질의 언어에서 고수준 언어로 원하는 동작을 표현하면, 구현(자바에서는 스트림 라이브러리가 이 역할을 수행) 에서 최적의 저수준 실행 방법을 선택하는 방식으로 동작.
- 스트림을 이용하면 에러를 자주 일으키며 멀티코어 CPU를 이용하는 것보다 비용이 훨씬 비싼 키워드 synchronized 를 사용하지 않아도 된다.

메서드에 코드를 전달하는 기법을 이용하면 새롭고 간결한 방식으로 동작 파라미터화(behavior parameterization)를 구현할 수 있다.
- 예를 들어 액션만 다른 두 메서드가 있다고 가정할 때 두 메서드를 그대로 유지하는 것 보다는 인수를 이용해서 다른 동작을 하도록 하나의 메서드로 합치는 것이 바람직 할 수 있다.
    - 그러면 복사 및 붙여넣기는 하는 기법에 비해 프로그램이 짧고 간결해지며, 불필요한 에러도 줄일 수 있다.

자바 8 기법은 함수형 프로그래밍에서 위력을 발휘한다. 코드를 전달하거나 조합해서 자바의 강력한 프로그래밍 도구로 활용할 수 있다는 것을 이 책 전반에서 확인 가능하다.

---
## 1.2. 왜 아직도 자바는 변화하는가?
### 1.2.1. 프로그래밍 언어 생태계에서 자바의 위치
자바는 처음부터 많은 유용한 라이브러리를 포함하는 잘 설계된 객체지향 언어로 시작했다. 
또한 처음부터 스레드와 락을 이용한 소소한 동시성도 지원했다.

코드를 JVM 바이트 코드로 컴파일하는 특징 때문에 자바는 인터넷 애플릿 프로그램의 주요 언어가 되었다.

자바 가상 머신(JVM) 과 바이트 코드를 자바 언어보다 중요시하는 일부 애플리케이션에서는 JVM 에서 실행되는 경쟁 언어인 스칼라, 그루비 등이 자바를 대체했다. 

자바는 다양한 임베디드 컴퓨팅 분야를 성공적으로 장악하고 있다.


### 1.2.2. 스트림 처리
 - 조립라인 처럼 스트림 API는 파이프라인을 만드는 데 필요한 많은 메서드를 제공한다. 
 - 스레드라는 복잡한 작업을 사용하지 않으면서 공짜로 병렬성을 얻을 수 있다.
### 1.2.3. 동작 파라미터화로 메서드에 코드 전달하기
  - 메서드(우리코드) 를 다른 메서드의 인수로 넘겨주는 기능을 제공한다. 이러한 기능을 이론적으로 동작 파라미터화(behavior parameterization) 라고 부른다.
  - 스트림 API 는 연산의 동작을 파라미터화 할 수 있는 코드를 전달한다는 사상에 기초하기 때문에 중요하다.
### 1.2.4. 병렬성과 공유 가변 데이터
  - 병렬성을 공짜로 얻을 수 있지만 이를 얻기 위해 포기해야 하는 점이 있다. 
    - 공유된 가변 데이터(shared mutable data)에 접근하지 않아야 한다.
    - 이러한 함수를 순수 함수, 부작용 없는 함수, 상태 없는 함수라 부르며 18장과 19장에서 이들 함수를 자세히 설명한다.
### 1.2.5. 자바가 진화해야 하는 이유
 - 자바는 새로운 기능을 추가하면서 인기 언어의 자리를 유지하고 있다. 자바 8의 새로운 기능에 관심을 가짐으로써 스스로 자바 프로그래머로서의 삶을 유지할 수 있도록 자신을 보호할 수 있다. 
 - 자바8을 사용했던 모든 사람에게 자바 8 이전으로 돌아가고 싶은지 물어보면?
 - 또한 자바 8에 추가된 새로운 기능 덕분에 기존에 다른 언어가 담당하던 생태계 영역을 자바 8이 정복하면서 자바 8 프로그래머에게는 더 많은 기회가 열릴 것.

---
## 1.3. 자바 함수
자바 8에서 함수 사용법은 일반적인 프로그래밍 언어의 함수 사용법과 아주 비슷하다. 프로그래밍 언어의 핵심은 값을 바꾸는 것이다. 전통적으로 프로그래밍 언어에서는 이 값을 일급(first-class) 값(또는 시민)이라고 부른다. 이전까지 자바 프로그래밍 언어에서는 기본값, 인스턴스만이 일급 시민이었다. 메서드와, 클래스는 이 당시 일급 시민이 아니었는데, 런타임에 메서드를 전달할 수 있다면, 즉 메서드를 일급 시민으로 만들면 프로그래밍에 유용하게 활용될 수 있다. 자바 8 설계자들은 이급 시민을 일급 시민으로 바꿀 수 있는 기능을 추가했다.
### 1.3.1. 메서드와 람다를 일급 시민으로
 - 메서드 참조 
   - 동작의 전달을 위해 익명클래스로 만들고 메서드를 구현해서 넘길 필요 없이, 준비된 함수를 메서드 참조(::) 를 이용해서 전달할 수 있다. 
   - 아래 예제를 통해 자바 8에서는 더이상 메서드가 이급값이 아닌 일급값인것을 확인할 수 있다. 


 - 익명 클래스를 통한 파일 리스팅.
~~~java
File[] hiddenFiles = new File(".").listFiles(new FileFilter() {
    public boolean accept(File file) {
        return file.isHidden(); // 숨겨진 파일 필터링.
    } 
});
~~~ 


 - 메서드 참조를 이용한 파일 리스팅
~~~java
File[] hiddenFiles = new File(".").listFiles(File::isHidden);
~~~

 - 이미 isHidden 이라는 함수는 준비되어 있으므로 자바 8의 메서드 참조(::)를 이용해서 listFiles 에 직접 전달할 수 있다. 
 - 메서드 참조 개념은 3장에서 자세히 설명.

#### 람다 : 익명함수
- 자바 8에서는 (기명 named)메서드를 일급값으로 취급할 뿐 아니라 람다(anonymous function)를 포함하여 함수도 값으로 취급할 수 있다.
- ex : (int x) -> x + 1, 즉 'x 라는 인수로 호출하면 x +1을 반환' 하는 동작을 수행하도록 코드 구현 가능.
 

### 1.3.2. 코드 넘겨주기 : 예제
java 8 이전에는 아래와 같이 구체적인 메서드 이름으로 구현했을 것이다. 
~~~java
...
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if ("green".equals(apple.getColor())) {
        result.add(apple);
      }
    }
    return result;
  }

  public static List<Apple> filterHeavyApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getWeight() > 150) {
        result.add(apple);
      }
    }
    return result;
  }
        ...
~~~

다행히 자바 8에서는 코드를 인수로 넘겨줄 수 있으므로 filter 메서드를 중복으로 구현할 필요가 없다. 

자바 8에 맞게 구현 가능.

~~~java

public class FilteringApples {
    public static void main(String[] args) {
        List<Apple> apples = Arrays.asList(new Apple(80, "green")
                , new Apple(155, "green")
                , new Apple(120, "red"));

        // [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
        List<Apple> greenApples = filterApples(apples, FilteringApples::isGreenApple);
        System.out.println(greenApples);

        // [Apple{color='green', weight=155}]
        List<Apple> heavyApples = filterApples(apples, FilteringApples::isHeavyApple);
        System.out.println(heavyApples);

        // [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
        List<Apple> greenApples2 = filterApples(apples, (Apple a) -> "green".equals(a.getColor()));
        System.out.println(greenApples2);

        // [Apple{color='green', weight=155}]
        List<Apple> heavyApples2 = filterApples(apples, (Apple a) -> a.getWeight() > 150);
        System.out.println(heavyApples2);

        // []
        List<Apple> weirdApples = filterApples(apples, (Apple a) -> a.getWeight() < 80 || "brown".equals(a.getColor()));
        System.out.println(weirdApples);
    }

    private static boolean isHeavyApple(Apple apple) {
        return apple.getWeight() > 150;
    }

    private static boolean isGreenApple(Apple apple) {
        return apple.getColor().equals("green");
    }

    public static List<Apple> filterApples(List<Apple> apples, Predicate<Apple> p) {
        List<Apple> filterApples = new ArrayList<>();
        for (Apple apple : apples) {
            if (p.test(apple)) {
                filterApples.add(apple);
            }
        }
        return filterApples;
    }
}

~~~


#### 프래디케이트(predicate) 란 무엇인가?
 - 수학에서는 인수로 값을 받아 true, false를 반환하는 함수를 프레디케이트 라고 한다. 나중에 설명하겠지만 자바 8에서도 Function<Apple, Boolean> 같이 코드를 구현할 수 있지만 Predicate<Apple> 을 사용하는 것이 더 표준적인 방식이다.
 
~~~java
public interface Predicate<T> {
    boolean test(T t);
}
~~~

### 1.3.3. 메서드 전달에서 람다로
 - 메서드를 값으로 전달하는 것은 분명 유용한 기능이다. 하지만 isHeavyApple, isGreenApple 처럼 한 두번만 사용할 메서드를 매번 적의하는 것은 귀찮은 일이다. 
 - 자바 8에서는 다음처럼 (익명 함수 또는 람다라는) 새로운 개념을 이용해서 코드를 구현할 수 있다.

~~~java
filterApples(inventory, (Apple a) -> GREEN.equals(a.getColor()) );
~~~

- 또는 다음과 같이 구현한다.
~~~java
filterApples(inventory, (Apple a) -> a.getWeight() > 150 );
~~~

- 심지어 다음과 같이 구현도 가능.
~~~java
filterApples(inventory, (Apple a) -> a.getWeight() < 80 || RED.equals(a.getColor()) );
~~~
 - 람다가 몇 줄 이상으로 길어진다면 위 방법 대신 일을 잘 설명하는 이름을 가진 메서드를 정의하고 메서드 참조를 활용하는 것이 바람직하지만 간간하다면 위와 같이 하는게 효율적이다. 


멀티코어 CPU가 아니었다면 자바 8 설계자들의 계획은 여기까지 였을 것이다. 곧 살펴보겠지만 지금까지 등장한 함수형 프로그래밍이 얼마나 강력한지 증명해왔다. 
아마도 자바는 filter 그리고 다음과 같은 몇몇 일반적인 라이브러리 메서드를 추가하는 방향으로 발전했을 수도 있었다. 

~~~java
static <T> Collection<T> filter(Collection<T> c, Predicate<T> p);
~~~

 - 다음처럼 라이브러리 메서드 filter 를 이용하면 filterApples 메서드를 구현할 필요가 없다.
~~~java
filter(inventory, (Apple a) -> a.getWeight() > 150);
~~~

---
## 1.4. 스트림

 - 스트림 API가 나오기 이전엔 컬렉션 API를 이용하여 다양한 로직을 처리하였을 것이다. 
 - 스트림 API를 이용하면 컬렉션 API와는 상당히 다른 방식으로 데이터를 처리할 수 있다. 
   - 컬렉션 API를 사용하면 for-each 루프를 이용하여 각 요소를 반복하면서 작업을 수행했다. 
     - 이러한 방식의 반복을 외부 반복(external iteration)이라 한다. 
   - 반면 스트림 API를 이용하면 루프를 신경 쓸 필요가 없다. 
     - 스트림 API에서는 라이브러리 내부에서 모든 데이터가 처리된다. 
       - 이와 같은 반복을 내부 반복(internal iteration)이라고 한다.

### 1.4.1. 멀티스레딩은 어렵다.
 - 자바 8은 스트림 API(java.util.stream)로 '컬렉션을 처리하면서 발생하는 모호함과 반복적인 코드문제' 그리고 '멀티코어 활용 어려움' 이라는 두 가지 문제를 모두 해결했다. 
 - 자주 반복되는 패턴으로 주어진 조건에 따라 데이터를 "필터링", 데이터를 "추출", 데이터를 "그룹화" 등의 기능이 있다.
 - 컬렉션을 필터링 할 수 있는 가장 빠른 방법
   - 컬렉션을 스트림으로 바꾸고, 병렬로 처리한 다음에, 리스트로 다시 복원하는 것. 
## 1.5. 디폴트 메서드와 자바 모듈
 - 기존 자바 기능으로는 대규모 컴포넌트 기반 프로그래밍 그리고 진화하는 시스템의 인터페이스를 적절히 대응하기 어려웠다. 
   - 자바 8에서 지원하는 디폴트 메서드를 이용해 기존 인터페이스를 구현하는 클래스를 바꾸지 않고도 인터페이스를 변경할 수 있다.
 - 예를 들어 List나 Collection 인터페이스는 이전에 stream이나 parallelStream 메서드를 지원하지 않았다. 
   - 하지만 자바 8에서 Collection 인터페이스에 stream메서드를 추가하고 이를 디폴트 메서드로 제공하여 기존 인터페이스를 쉽게 변경할 수 있었다.

## 1.6. 함수형 프로그래밍에서 가져온 다른 유용한 아이디어
 - 자바 8에서는 NPE(NullPointerException)을 피할 수 있도록 도와주는 Optional<T> 클래스를 제공한다.
   - 값이 없는 상황을 어떻게 처리할지 명시적으로 구현하는 메서드도 포함하고 있다.
## 1.7. 마치며


- 참고링크 
  - https://github.com/ckddn9496/modern-java-in-action/blob/main/contents/Chapter%201%20-%20%EC%9E%90%EB%B0%94%208%2C%209%2C%2010%2C%2011%20%EF%BC%9A%20%EB%AC%B4%EC%8A%A8%20%EC%9D%BC%EC%9D%B4%20%EC%9D%BC%EC%96%B4%EB%82%98%EA%B3%A0%20%EC%9E%88%EB%8A%94%EA%B0%80.md