# 2. 동작 파라미터화 코드 전달하기.

---
변화하는 요구사항은 소프트웨어 엔지니어링에서 피할 수 없는 문제다. 예를 들어 농부가 재고목록 조사를 쉽게 할 수 있도록 돕는 애플리케이션이 있다고 가정하자.
농부는 이렇게 말할 것이다. "녹색 사과를 모두 찾고 싶어요" 그런데 하룻밤을 자고 일어났더니 농부가 다시 이렇게 말하는 것이다. "150 그램 이상인 사과를 모두 찾고 싶어요" 또 하룻밤을 자고 일어났더니.. "150 그램 이상이면서 녹색인 사과를 모두 찾을 수 있다면 좋겠네요." 라고 말하는 것이었다.

위 이유로 인해 새로 추가한 기능은 쉽게 구현할 수 있어야 하며 장기적인 관점에서 유지보수가 쉬워야 한다.

"동작 파라미터화(behavior parameterization)" 를 이용하면 자주 바뀌는 요구사항에 효과적으로 대응할 수 있다. 
 - 동작 파라미터화(behavior parameterization)란 아직은 어떻게 실행할 것인지 결정하지 않은 코드 블록을 의미한다. 
   - 이 코드 블록은 나중에 프로그램에서 호출한다. 즉 코드 블록의 실행은 나중으로 미뤄진다.
   
동작 파라미터로화로 아래와 같이 다양한 기능을 수행할 수 있음,
 - EX) 컬렉션을 처리할 때 다음과 같은 메서드를 구현한다고 가정. 
   - 리스트의 모든 요소에 대해서 '어떤 동작' 을 수행할 수 있음.
   - 리스트 관련 작업을 끝낸 다음에 '어떤 다른 동작' 을 수행할 수 있음.
   - 에러가 발생하면 '정해진 어떤 다른 동작' 을 수행할 수 있음.

## 2.1. 변화하는 요구사항에 대응하기

---
 - 하나의 예제를 선정한 다음 예제 코드를 점차 개선하면서 유연한 코드를 만드는 모범사례를 보여줄 것이다.
### 2.1.1. 첫 번째 시도 : 녹색 사과 필터링

---

- 사과 색 정의
~~~java
enum Color {RED, GREEN}
~~~

 - 첫번째 시도 결과 코드
~~~java
...
    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (GREEN.equals(apple.getColor())) { // 녹색 사과만 선택
                result.add(apple);
            }
        }
        return result;
    }...
~~~

 - 갑자기 농부가 변심하여 녹색 사과 말고 빨간 사과도 필터링하고 싶어졌을 때?
   - 크게 고민하지 않는다면 위 메서드를 복사해서 filterRedApples 메서드 만들고 if 조건 줄 수도 있음.
   - 하지만 농부가 좀 더 다양한 색으로 필터링하는 등의 변화에는 적절하게 대응 불가. 
   - 이 상황에서 다음과 같은 좋은 규칙이 있다.
     - 거의 비슷한 코드가 반복 존재한다면 그 코드를 추상화한다. 

### 2.1.2. 두 번째 시도 : 색을 파라미터화

---
 - 색 파라미터화 할 수 있도록 메서드에 색 파라미터 추가. 
~~~java
...
    public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getColor().equals(color)) {
                result.add(apple);
            }
        }
        return result;
    }
        ...
~~~

 - 구현한 메서드 호출
~~~java
  List<Apple> greenApples1 = filterApplesByColor(inventory, GREEN);
  System.out.println(greenApples1);

  List<Apple> redApples1 = filterApplesByColor(inventory, RED);
  System.out.println(redApples1);
~~~

 - 색 이외에도 가벼운 사과와 부거운 사과로 구분할 수 있도록 요구사항 추가되었을 경우. 
   - 그래서 아래와 같이 무게 정보 파라미터 추가.
~~~java
...
    public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (apple.getWeight() > weight) {
                result.add(apple);
            }
        }
        return result;
    }
...
~~~

- 위 코드도 좋은 해결책이지만.. 구현 코드를 자세히 보면 목록을 검색하고, 각 사과에 필터링 조건을 적용하는 부분의 코드가 색 필터링 코드와 대부분 중복.
- 이는 소프트웨어 공학의 DRY(don't repeat yourself, 같은 것을 반복하지 말것) 위반.
- 색과 무게를 filter라는 메서드로 합치는 방법도 있다. 그러면 어떤 기준으로 사과를 필터링 할 지 구분하는 또다른 방법 필요. 
  - 따라서 색이나 무게 중 어떤 것을 기준으로 필터링 할지 가리키는 플래그를 추가할 수 있음.
    - 하지만 실전에서는 절대 이 방법을 사용하지 말아야 한다. 이유는 조금 뒤에 설명.
    
### 2.1.3. 세 번째 시도 : 가능한 모든 속성으로 필터링

---
만류에도 불구하고 모든 속성을 메서드 파라미터로 추가한 모습.

~~~java
public static List<Apple> filterApples(List<Apple> inventory, Color color, int weight, boolean flag) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if ((flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight)) {
            result.add(apple);
        }
    }
    return result;
}
~~~

 - 다음처럼 위 메서드를 사용 (정말 마음에 들지 않는 코드.)
~~~java
List<Apple> greenApples2 = filterApples(inventory, GREEN, 0, true);
System.out.println(greenApples2);

List<Apple> heavyApples2 = filterApples(inventory, null, 150, false);
System.out.println(heavyApples2);
~~~

위 코드는 형편없는 코드임.
 - true, false는 무엇을 의미? 
 - 요구사항이 바뀌었을 때 유연하게 대응 불가. 
   - 사과의 크기, 모양, 출하지 등으로 사과 필터링 불가.

filterApples 에 어떤 기준으로 사과를 필터링할 것인지 효과적으로 전달할 수 있으면 더 좋을 것.

## 2.2. 동작 파라미터화
 - 사과의 어떤 속성에 기초해서 불리언 값을 반환(예를 들어 사과가 녹색인가? 150 그램 이상인가?) 하는 방법이 있음.
 - 참 거짓을 반환하는 함수를 predicate 라고 한다. 


선택 조건을 결정하는 (전략)인터페이스 선언
~~~java
public interface ApplePredicate {
    boolean test(Apple apple);
}
~~~

클래스를 통한 동작 파라미터화
~~~java
public class AppleGreenColorPredicate implements ApplePerdicate {
    public boolean test(Apple apple) {
        return GREEN.equals(apple.getColor());
    }
}
~~~
~~~java 
List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate());
~~~

익명 클래스를 통한 동작 파라미터화
~~~java
List<Apple> greenApples = filterApples(inventory, new AppleGreenColorPredicate() {
    public boolean test(Apple apple) {
        return GREEN.equals(apple.getColor());
    }
});
~~~

filterApples 에서 ApplePredicate 객체를 받아 애플의 조건을 검사하도록 메서드를 고침. 이렇게 동작 파라미터화, 즉 메서드가 다양한 동작(또는 전약)을 받아서 내부적으로 다양한 동작을 수행할 수 있다.


### 2.2.1. 네 번째 시도 : 추상적 조건으로 필터링

ApplePredicate 를 이용한 필터 메서드 

~~~java
 public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
        if (p.test(apple)) {
            result.add(apple);
        }
    }
    return result;
}
~~~

람다를 통한 동작 파라미터화
~~~java
List<Apple> greenApples = filterApples(inventory, (Apple apple) -> GREEN.equals(apple.getColor()));
~~~

#### 한개의 파라미터, 다양한 동작.
 - 컬랙션 탐색 로직과 각 항목에 적용할 동작을 분리할 수 있다는 것이 동작 파라미터화의 강점.

## 2.3. 복잡한 과정 간소화
### 2.3.1. 익명 클래스
 - 자바의 지역 클래스 (블록 내부에 선언된 클래스) 와 비슷한 개념.
 - 이름이 없는 클래스
 - 익명클래스 이용 시 클래스 선언과 인스턴스화 동시에 가능.

### 2.3.2. 다섯 번째 시도 : 익명 클래스 사용

~~~java
  List<Apple> greenApples3 = filterApples(inventory, new ApplePredicate() {
        @Override
        public boolean test(Apple apple) {
            return GREEN.equals(apple.getColor());
        }
    });
    System.out.println(greenApples3);
~~~

 - 익명 클래스로 인터페이스를 구현하는 여러 클래스를 선언하는 과정을 조금 줄일 수 있으나 만족스럽지 않음.
### 2.3.3. 여섯 번째 시도 : 람다 표현식 사용
람다를 통한 동작 파라미터화
~~~java
List<Apple> greenApples = filterApples(inventory, (Apple apple) -> GREEN.equals(apple.getColor()));
~~~

### 2.3.4. 일곱 번째 시도 : 리스트 형식으로 추상화
~~~java
public interface Predicate<T> {
    boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> result = new ArrayList<>();
    for(T e : list) {
        if(p.test(e)) {
            result.add(e);
        }
    }
    return result;
}
~~~

~~~java
List<Apple> redApples = filter(inventory, apple -> RED.equals(apple.getColor()));
System.out.println(redApples);

List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
List<Integer> evenNumbers = filter(numbers, (Integer i) -> i % 2 == 0);
System.out.println(evenNumbers);
~~~
위와 같은 방법으로 유연성과 간결함이라는 두 마리 토끼를 모두 잡을 수 있다.


## 2.4. 실전 예제
### 2.4.1. Comparator로 정렬하기
~~~java
// java.util.Comparator
public interface Comparator<T> {
	int compare(T o1, T o2)l
}
~~~
Comparator를 구현해서 sort 메서드의 동작을 다양화 할 수 있음.
~~~java
// anonymous class
inventory.sort(new Comparator<Apple>() {
	public int compare(Apple a1, Apple a2) {
		return a1.getWeight().compareTo(a2.getWeight());
	}
});

// lamda
inventory.sort(
	(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
~~~

### 2.4.2. Runnable로 코드 블록 실행하기
~~~java
public interface Runnable {
    void run();
}
~~~
Runnable 을 이용해서 다양한 동작을 스레드로 실행할 수 있다. 
~~~java
//  anonymous class
Thread t = new Thread(new Runnable() {
    public void run() {
        System.out.println("hello world");
    }
})

// lamdba 
Thread t = new Thread(() -> System.out.println("hello world"));

~~~
### 2.4.3. Callable 을 결과로 반환하기.

~~~java
public interface Callable<V> {
    V call();
}

//  anonymous class
ExecutorService executorService = Executors.newCachedThreadPool();
Future<String> threadName = executorService.submit(new Callable<String>() {
    @Override
    public String call() throws Exception {
        return Thread.currentThread().getName();
    }
});

// lamdba
Future<String> threadName = executorService.submit(() -> Thread.currentThread().getName());

~~~
### 2.4.4. GUI 이벤트 처리하기.

~~~java

//  anonymous class
Button button = new Button("Send");
button.setOnAction(new EventHandler<ActionEvent> () {
    public void handle(ActionEvent event) {
        label.setText("Sent!!");    
    }
});

// lamdba
button.setOnAction((ActionEvent event) -> label.setText("Sent!!"));
~~~


## 2.5. 마치며
 - 동작 파라미터화에서는 메서드 내부적으로 다양한 동작을 수행할 수 있도록 코드를 메서드 인수로 전달.
 - 동작 파라미터화를 이용하면 변화하는 요구사항에 더 잘 대응할 수 있는 코드를 구현할 수 있으며 엔지니어링 비용을 줄일 수 있음.
 - 코드 전달 기법을 이용하면 동작을 메서드의 인수로 전달할 수 있다.
   - 하지만 자바 8 이전에는 코드를 지저분하게 구현해야 했다.
   - 자바 8에서는 인터페이스를 상속받아 여러 클래스를 구현해야 하는 수고를 없앨 수 있는 방법을 제공한다.
 - 자바 API의 많은 메서드는 정렬, 스레드, GUI 처리 등을 포함한 다양한 동작으로 파라미터화 할 수 있다.