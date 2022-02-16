# 9. 리팩터링, 테스팅, 디버깅
 - 람다 표현식을 이용해 가독성과 유연성을 높이려면 기존 코드를 어떻게 리팩토링 해야 하는지
 - 람다 표현식으로 전략, 템플릿 메서드, 옵저버, 의무체인, 팩토리 등의 객체지향 디자인 패턴을 어떻게 간소화할 수 있는지도 살펴본다.
 - 람다 표현식과 스트림 API를 사용하는 코드를 테스트하고 디버깅하는 방법을 설명한다. 

## 9.1. 가독성과 유연성을 개선하는 리팩터링
### 9.1.1. 코드 가독성 개선
 - 코드 가독성을 개선한다는 것은 우리가 구현한 코드를 다른 사람이 쉽게 이해하고 유지보수 할 수 있게 만드는 것을 으미ㅣ
### 9.1.2. 익명 클래스를 람다 표현식으로 리팩터링하기
 - 익명 클래스는 코드를 장황하게 만들고 쉽게 에러를 일으킬 수 있다.
   - 람다 표현식을 이용해서 간결하고 가독성이 좋은 코드를 구현할 수 있었다.
~~~java
Runnable r1 = new Runnable() {
    public void run() {
        System.out.println("Hello");        
    }
}
~~~
~~~java
Runnable r1 = () -> System.out.println("Hello");
~~~
 - 익명 클래스에서 사용한 this와 super는 람다 표현식에서 다른 의미를 갖는다.
   - 익명 클래스에서 this는 익명 클래스에서 사진을 가지키지만 람다에서 this는 람다를 감싸는 클래스를 가리킴
   - 익명 클래스는 감싸도 있는 클래스의 변수를 가릴 수 있다. (섀도 변수)
   - 익명 클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따른 모호함이 초래 될 수 있다.
     - 람다의 형식은 콘텍스트에 따라 달라지기 때문

이와 같은 문제가 일어날 수 있음을 보여주는 예제
~~~java
interface Task {
     void execute();
}

public static void doSomething(Runnable r) { r.run(); }
public static void doSomething(Task r) { r.execute(); }
~~~
Task 를 구현하는 익명 클래스를 전달할 수 있다.
~~~java
doSomething(new Tash() {
    public void execute() {
        System.out.println("Danger danger!!");
    }       
});
~~~

하지만 익명 클래스를 람다 표현식으로 바꾸면 메서드를 호출할 때 Runnable 과 Task 모두 대상 형식이 될 수 있으므로 문제가 생긴다.
 - 해당 경우 명시적 형변환 (Task)를 이용해서 모호함을 제거할 수 있다.
~~~java
doSomething((Task)() -> System.out.println("Danger danger!!"));
~~~

### 9.1.3. 람다 표현식을 메서드 참조로 리팩터링하기
 - 람다 표현식 대신 메서드 참조를 이용하면 가독성을 넢일 수 있다.
 - comparing과 maxBy 같은 정적 헬퍼 메서드를 활용하는 것도 좋다. 
 - 내장 컬렉터(summingInt) 를 이용하면 코드 자체로 문제를 더 명확하게 설명할 수 있다.
### 9.1.4. 명령형 데이터 처리를 스트림으로 리팩터링하기
  - 스트림 API를 이용하면 문제를 더 직접적으로 기술할 수 있을 뿐 아니라 쉽게 병렬화할 수 있다.
### 9.1.5. 코드 유연성 개선
  - 람다 표현식을 이용하면 동작 파라미터화(behavior parameterzation)를 쉽게 구현할 수 있다.
    - 람다 표현식을 이용하려면 함수형 인터페이스 필요.
#### 조건부 연기 실행
  - 실제 작업을 처리하는 코드 내부에 제어 흐름문이 복잡하게 얽힌 코드를 흔히 볼 수 있다.
  - 흔히 보안 검사나 로깅 관련 코드가 이처럼 사용된다.
~~~java
if(logger.isLoggable(Log.FINER)) {
logger.finer("Problem: " + generateDiagnostic());
}
~~~
 - 위 코드는 다음과 같은 사항에 문제가 있다.
   - logger의 상태가 isLoggable 이라는 메서드에 의해 클라이언트 코드로 노출된다.
   - 메세지를 로깅할 때마다 logger 객체의 상태를 매번 확인? 코드를 어지럽힐 뿐이다.
 - logger 객체가 적절한 수준으로 설정되었는지 내부적으로 확인하는 log 메서드를 사용하는 것이 바람직하다.
~~~java
logger.log(Level.FINER, "Problem: " + generateDiagnostic());
~~~
  - 덕분에 불필요한 if 문을 제거할 수 있으며 logger의 상태를 노출할 필요도 없으므로 위 코드가 더 바람직한 구현이다.


 - 특정 조건에서만 메세지가 생성될 수 있도록 메세지 생성 과정을 연기(defer)할 수 있어야 한다.
   - 자바 8 API 설계자는 이와 같은 logger 문제를 해결할 수 있도록 Supplier를 인수로 갖는 오버로드된 log 메서드를 제공했다.
~~~java
public void log(Level level, Supplier<String> msgSupplier);
~~~

메서드 호출
~~~java
logger.log(Level.FINER, () -> "Problem : " + generateDiagnostic())
~~~

내부구현
~~~java
public void log(Level level, Supplier<String> msgSupplier) {
    if(logger.isLoggable(level)) {
        log(level, msgSupplier.get()); // 람다 실행    
    }    
}
~~~

#### 실행 어라운드
 - 매번 같은 준비, 종료 과정을 반복적으로 수행한다면 이를 람다로 변환할 수 있다.
 - 준비, 종료 과정을 처리하는 로직을 재 사용함으로써 코드 중복을 줄일 수 있다.

## 9.2. 람다로 객체지향 디자인 패턴 리팩터링하기
 - 디자인 패턴에 람다 표현식이 더해지면 색다른 기능을 발휘할 수 있다.
   - 즉 람다를 이용하면 이전에 디자인 패턴으로 해결하던 문제를 더 쉽고 간단하게 해결할 수 있다.
   - 또한 람다 표현식으로 기존의 많은 객체지향 디자인 패턴을 제거하거나 간결하게 재구현할 수 있다.
### 9.2.1. 전략
 - 한 유형의 알고리즘을 보유한 상태에서 런타임에 적절한 알고리즘을 선택하는 기법
 - 전략패턴은 세 부분으로 구성
   - 알고리즘을 나타내는 인터페이스(Strategy 인터페이스)
   - 다양한 알고리즘을 나타내는 한 개 이상의 인터페이스 구현(ConcreteStrategyA, ConcreteStrategyB 같은 구체적인 구현 클래스 )
   - 전략 객체를 사용하는 한 개 이상의 클라이언트

 - String 문자열 검증 인터페이스 구현
~~~java
public interface ValidationStrategy {
    boolean execute(String s);
}
~~~

 - 위에서 정의한 인터페이스를 구현하는 클래스를 하나 이상 정의
~~~java
public class IsAllLowerCase implements ValidationStrategy {
    @Override
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}

~~~
~~~java
public class IsNumeric implements ValidationStrategy {
    @Override
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}

~~~
지금까지 구현한 클래스를 다양한 검증 전략으로 활용 가능
~~~java
public class Validator {
    private final ValidationStrategy strategy;

    public Validator(ValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(String s) {
        return strategy.execute(s);
    }
}
~~~
~~~java
public static void main(String[] args) {
     Validator numericValidator = new Validator(new IsNumeric());
     boolean b1 = numericValidator.validate("aaaa");
     System.out.println(b1);

     Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
     boolean b2 = lowerCaseValidator.validate("bbbb");
     System.out.println(b2);
 }
~~~

#### 람다 표현식 사용
 - ValidateStrategy 는 함수형 인터페이스며 Predicate<String> 과 같은 함수 디스크립터를 갖고 있음을 파악했을 것이다.
 - 다양한 전략을 구현하는 새로운 클래스를 구현할 필요 없이 람다 표현식을 직접 전달하면 코드가 간결해진다.
~~~java
Validator numericValidator = new Validator(s -> s.matches("\\d+"));
boolean b1 = numericValidator.validate("aaaa");
System.out.println(b1);

Validator lowerCaseValidator = new Validator(s -> s.matches("[a-z]+"));
boolean b2 = lowerCaseValidator.validate("bbbb");
System.out.println(b2);
~~~
### 9.2.2. 템플릿 메서드
 - 알고리즘의 개요를 제시한 다음에 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 템플릿 메서드 디자인 패턴을 사용한다.
   - 템플릿 메서드는 '이 알고리즘을 사용하고 싶은데 그대로는 안 되고 조금 고쳐야 하는' 상황에 적합하다.

~~~java
abstract class OnlineBanking {
    public void processCustomer(int id) {
        Customer c = Database.getCusromerWithId(id);
        makeCustomerHappy(c);
    }
    abstract void makeCustomerHappy(Customer c);
}
~~~
 - 각각의 지점은 OnlineBanking 클래스를 상속받아 makeCustomerHappy 메서드가 원하는 동작을 수행하도록 구현할 수 있다.
 
#### 람다 표현식 사용
~~~java
abstract class OnlineBanking {
    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
        Customer c = Database.getCusromerWithId(id);
        makeCustomerHappy.accept(c);
    }
    abstract void makeCustomerHappy(Customer c);
}
~~~

~~~java
new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello!"));
~~~

### 9.2.3. 옵저버
 - 어떤 이벤트가 발생했을 때 한 객체(주제(subject) 라 불리는) 가 다른 객체 리스트(옵저버(observer)) 에 자동으로 알림을 보내야 하는 상황에서 옵저버 디자인 패턴을 사용
 - 옵저버 패턴으로 트위터 같은 커스터마이즈된 알림 시스템을 설계하고 구현할 수 있다.


 - 우선 다양한 옵저버를 그룹화할 Observer 인터페이스가 필요하다. 
   - 새로운 트윗이 있을 때 주제(Feed)가 호출할 수 있도록 notify 라고 하는 하나의 메서드를 제공한다.
~~~java
interface Observer {
    void notify(String tweet);
}
~~~
 - 이제 트윗에 포함된 다양한 키워드에 다른 동작을 수행할 수 있는 여러 옵저버를 정의할 수 있다.
~~~java
public class NYTimes implements Observer {
    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("money")) {
            System.out.println("Breaking news in NY!" + tweet);
        }
    }
}

~~~
~~~java
public class Guardian implements Observer{
    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("queen")) {
            System.out.println("Yet more news from London..." + tweet);
        }
    }
}

~~~
~~~java
public class LeMonde implements Observer {
    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("wine")) {
            System.out.println("Today cheese, wine and news!" + tweet);
        }
    }
}
~~~
 - 주제도 구현해야 한다.
~~~java
interface Subject {
    void registerObserver(Observer o);
    void notifyObservers(String tweet);
}
~~~

 - 주제는 registerObserver 메서드로 새로운 옵저버를 등록한 다음에 notifyObservers 메서드로 트윗의 옵저버에 이를 알린다.
~~~java
public class Feed implements Subject {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyObservers(String tweet) {
        observers.forEach(o -> o.notify(tweet));
    }
}
~~~

 - 구현은 간단하다. Feed는 트윗을 받았을 떄 알림을 보낼 옵저버 리스트를 유지한다. 
~~~java
public class Main {
    public static void main(String[] args) {
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObservers("The queen said her favourite book is Modern Java in Action!");
    }
}
~~~
> 위 예제에선 가디언이 트윗을 받는다.

#### 람다 표현식 사용하기.
 - 옵저버 구현 클래스를 아래와 같이 재체
~~~java
Feed f2 = new Feed();
// 람다 표현식 사용
f2.registerObserver((String tweet) -> {
   if (tweet != null && tweet.contains("money")) {
       System.out.println("Breaking news in NY!" + tweet);
   }
});
f2.registerObserver((String tweet) -> {
   if (tweet != null && tweet.contains("queen")) {
       System.out.println("Yet more news from London..." + tweet);
   }
});
~~~

### 9.2.4. 의무 체인
 - 한 객체가 어떤 작업을 처리한 다음에 다른 객체로 결과를 전달하고, 다른 객체도 해야 할 작업을 처리한 다음에 또 다른 객체로 전달하는 식이다.
 - 작업 처리 객체가 자신의 작업을 끝냈으면 다음 작업 처리 객체로 결과를 전달한다.

 - 작업 처리 객체 예제 코드
~~~java
public abstract class ProcessingObject<T> {

    protected ProcessingObject<T> successor;

    public void setSuccessor(ProcessingObject<T> successor) {
        this.successor = successor;
    }

    public T handle(T input) {
        T r = handleWork(input);
        if (successor != null) {
            return successor.handle(r);
        }
        return r;
    }

    public abstract T handleWork(T input);

}
~~~

 - ProcessingObject 클래스를 상속받아 handleWork 메서드를 구현하여 다양한 종류의 작업 처리 객체를 만들 수 있다.
~~~java
public class HeaderTextProcessing extends ProcessingObject<String> {
    @Override
    public String handleWork(String input) {
        return "From Raoul, Mario and Alan: " + input;
    }
}

~~~

~~~java
public class SpellCheckerProcessing extends ProcessingObject<String> {
    @Override
    public String handleWork(String input) {
        return input.replaceAll("ladba", "lambda");
    }
}

~~~
 - 두 작업 처리 객체를 연결해서 작업 체인을 만을 수 있다.
~~~java

public class Main {
    public static void main(String[] args) {
        ProcessingObject<String> p1 =  new HeaderTextProcessing();
        ProcessingObject<String> p2 =  new SpellCheckerProcessing();
        p1.setSuccessor(p2);

        String result = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result);
    }
}

~~~


#### 람다 표현식 사용
 - `Function<String, String>`, 더 정확히 표현하면  `UnaryOperator<String>` 형식의 인스턴스로 표현할 수 있다.
   - andThen 메서드로 이들 함수를 조합해서 체인을 만들 수 있다.

~~~java
UnaryOperator<String> headerProcessing = (String input) -> "From Raoul, Mario and Alan: " + input;
UnaryOperator<String> spellCheckerProcessing = (String input) -> input.replaceAll("ladba", "lambda");

Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
String result = pipeline.apply("Aren't labdas really sexy?!!");
System.out.println(result);
~~~


### 9.2.5. 팩토리
 - 인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 팩토리 디자인 패턴을 사용한다.

~~~java
public class ProductFactory {
    public static Product createProduct(String name) {
        switch (name) {
            case "loan":
                return new Loan();
            case "stock":
                return new Stock();
            case "bond":
                return new Bond();
            default:
                throw new RuntimeException(" No such product " + name);
        }
    }
}
~~~
 - 여기서 Loan, Stock, Bond 는 모두 Product 의 서브형식이다. 
   - createProduct 메서드는 생산된 상품을 설정하는 로직을 포함할 수 있다.
   - 위 코드의 진짜 장점은 생성자와 설정을 외부로 노출하지 않음으로써 클라이언트가 단순하게 상품을 생산할 수 있다는 것 이다.
~~~java
Product p = ProductFactory.createProduct("loan");
~~~

#### 람다 표현식 사용
 - 생성자로 메서드 참조처럼 접근할 수 있다. 
   - 예:  Loan 생성자를 사용하는 코드 작성
~~~java
Supplier<Product> loanSupplier = Loan::new;
Product product = loanSupplier.get();
~~~

 - 아래 코드처럼 생성자로 연결하는 Map 을 만들어서 코드 재 구현
~~~java
final static Map<String, Supplier<Product>> map = new HashMap<>();

static {
  map.put("loan", Loan::new);
  map.put("stock", Stock::new);
  map.put("bond", Bond::new);
}
~~~
- 다양한 상품 인스턴스화
~~~java
public static Product createProductLambda(String name) {
  Supplier<Product> p = map.get(name);
  if (p != null) return p.get();
  throw new IllegalArgumentException("No such product " + name);
}
~~~

 - 하지만 팩토리 메서드 createProduct 가 상품 생성자로 여러 인수를 전달하는 상황에서는 이 기법을 적용하기 어렵다.
   - 단순한 Supplier 함수형 인터페이스로는 이 문제를 해결할 수 없음.
   - 예를 들어 세 인수(Integer 둘 , 문자열 하나)를 받는 상품의 생성자가 있다고 가정 시 세 인수를 지우너하려면 TriFunction 이라는 특별한 함수형 인터페이스를 만들어야 한다.
   - 결국 다음 코드처럼 Map 의 시그니처가 복잡해진다.
~~~java
public interface TriFunction<T, U, V, R> {
    R applu(T, U, V);
}
...
Map<String, TriFunction<Integer, Integer, String, Product>> map = new HashMap<>();
~~~

## 9.3. 람다 테스팅
 - 개발자의 최종 업무 목표는 제대로 작동하는 코드를 구현하는 것이지 깔끔한 코드를 구현하는 것이 아니다. 
 - 좋은 소프트웨어 공학자라면 프로그램이 의도대로 동작하는지 확인할 수 있는 단위 테스팅을 진행한다.


 - 다음처럼 그래픽 애플리케이션의 일부인 Point 클래스가 있다고 가정
~~~java

public class Point {

    private final int x;
    private final int y;
    
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point moveRightBy(int x) {
        return new Point(this.x + x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
~~~

 - moveRightBy 메서드가 의도한 대로 동작하는지 확인하는 단위 테스트

~~~java
@Test
void testMoveRightBy() {
  Point p1 = new Point(5, 5);
  Point p2 = p1.moveRightBy(10);
  assertEquals(15, p2.getX());
  assertEquals(5, p2.getY());
}
~~~

### 9.3.1. 보이는 람다 표현식의 동작 테스팅
 - 람다는 익명(결국 익명 함수) 이므로 테스트 코드 이름을 호출할 수 없다.
 - 따라서 필요하다면 람다를 필드에 저장해서 재사용할 수 있으며 람다의 로직을 테스트 할 수 있다.
 - 예를 들에 Point 클래스에 compareByXAndThenY 라는 정적 필드를 추가했다고 가정.

~~~java

public class Point {

   private final int x;
   private final int y;


   public final static Comparator<Point> compareByXAndThenY
           = comparing(Point::getX).thenComparing(Point::getY);
   ...
}
~~~
 - 람다 표현식은 함수령 인터페이스의 인스턴스를 생성한다는 사실을 기억하자.
   - 따라서 생성된 인스턴스의 동작으로 람다 표현식을 테스트 할 수 있다.
 - 다음은 Comparator 객체 compareByXAndThenY 에 다양한 인수로 compare 메서드를 호출하면서 예상대로 동작하는지 테스트 하는 코드

~~~java
@Test
void testComparingTwoPoints() throws Exception {
  Point p1 = new Point(10, 15);
  Point p2 = new Point(10, 20);
  int result = Point.compareByXAndThenY.compare(p1, p2);
  assertTrue(result < 0);
}
~~~
### 9.3.2. 람다를 사용하는 메서드의 동작에 집중하라
 - 람다의 목표는 정해진 동작을 다른 메서드에서 사용할 수 있도록 하나의 조각으로 캡슐화하는 것.
   - 그려러면 세부 구현을 포함하는 람다 표현식을 공개하지 말아야 한다.
   - 람다 표현식을 사용하는 메서드의 동작을 테스트함으로써 람다를 공개하지 않으면서도 람다 표현식을 검증할 수 있다.
 -  moveAllPointsRightBy 메서드 테스트 예시
~~~java
   public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
        return points.stream()
                .map(p -> new Point(p.getX() + x, p.getY()))
                .collect(toList());
    }
~~~

~~~java
 @Test
 void testMoveAllPointsRightBy() throws Exception {
     List<Point> points = List.of(new Point(5, 5), new Point(10, 5));
     List<Point> exceptedPoints = List.of(new Point(15, 5), new Point(20, 5));

     List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
     assertEquals(exceptedPoints, newPoints);
 }
~~~
### 9.3.3. 복잡한 람다를 개별 메서드로 분할하기
 - 복잡한 람다 표현식을 어떻게 테스트할 것인가? 한가지 해결책은 람다 표현식을 메서드 참조로 바꾸는 것.
 - 복잡한 람다 표현식이라면 개별 메서드로 분할하여 테스트 단위를 잘게 쪼개는 것이다. 또한 함수를 인수로 받거나 다른 함수를 반환하는 메서드라면 테스트 내에서 또 다른 람다를 사용해서 테스트하자.

### 9.3.4. 고차원 함수 테스팅
 - 함수를 인수로 받거나 다른 함수를 반환하는 메서드(이를 고차원 함수 라고 하며 19장에서 자세히 설명) 는 좀 더 사용하기 어렵다.
 - 메서드가 람다를 인수로 받는다면 다른 람다로 메서드의 동작을 테스트할 수 있다.

## 9.4. 디버깅
 - 람다 표현식과 스트림은 기존의 디버깅 기법을 무력화한다. 
### 9.4.1. 스택 트레이스 확인
 - 람다 표현식은 이름이 없기 때문에 조금 복잡한 스택 트레이스가 생성된다.
 - 람다 표현식과 관련된 스택 트레이스는 이해하기 어려울 수 있다는 점을 염두해 주다.
   - 이는 미래의 자바 컴파일러가 개선해야 할 부분이다.
### 9.4.2. 정보 로깅


~~~java
List<Integer> numbers = Arrays.asList(2,3,4,5);

numbers.stream()
        .map(x -> x + 17)
        .filter(x -> x % 2 == 0)
        .limit(3)
        .forEach(System.out::println)
~~~
결과
~~~java
20
22
~~~

 - forEach를 통해 스트림 결과를 출력하거나 로깅할 수 있다. 
 - 하지만 forEach는 스트림을 소비하는 연산이다. 
 - 스트림 파이프라인에 적용된 각각의 연산의 결과를 확인할 수 있다면 대신 peek라는 스트림 연산을 활용할 수 있다. 
 - peek는 스트림의 각 요소를 소비한것 처럼 동작을 실행하지만, 실제로 스트림을 소비하지않고 자신이 확인한 요소를 파이프라인의 다음 연산으로 그대로 전달한다.

peek 사용예시
~~~java
  List<Integer> numbers = Arrays.asList(2, 3, 4, 5);

  List<Integer> result = numbers.stream()
  .peek(x -> System.out.println("from stream: " + x))
  .map(x -> x + 17)
  .peek(x -> System.out.println("after map: " + x))
  .filter(x -> x % 2 == 0)
  .peek(x -> System.out.println("after filter: " + x))
  .limit(3)
  .peek(x -> System.out.println("from limit: " + x))
  .collect(toList());


  System.out.println("====");
  System.out.println(result);
~~~

결과
~~~java
from stream: 2
after map: 19
from stream: 3
after map: 20
after filter: 20
from limit: 20
from stream: 4
after map: 21
from stream: 5
after map: 22
after filter: 22
from limit: 22
====
[20, 22]
~~~

## 9.5. 마치며
 - 람다 표현식으로 가독성이 좋고 더 유연한 코드를 만들 수 있다.
 - 익명 클래스는 람다 표현식으로 바꾸는 것이 좋다. 하지만 이때 this, 변수 섀도 등 미묘하게 의미상 다른 내용이 있음을 주의하자.
 - 메서드 참조로 람다 표현식보다 더 가독성이 좋은 코드를 구현할 수 있다.
 - 반복적으로 컬렉션을 처리하는 루틴은 스트림 API 로 대체할 수 있을지 고려하는 것이 좋다.
 - 람다 표현식으로 전략, 템플릿 메서드, 옵저버, 의무 체인, 팩토리 등의 객체지향 디자인 패턴에서 발생하는 불필요한 코드를 제거할 수 있다.
 - 람다 표현식도 단위 태스트를 수행할 수 있다. 하지만 람다 표현식 자체를 테스트하는 것 보다는 람다 표현식이 사용되는 메서드의 동작을 테스트하는 것이 바람직하다.
 - 복잡한 람다 표현식은 일반 메서드로 재구현할 수 있다.
 - 람다 표현식을 사용하면 스택 트레이스를 이해하기 어려워진다.
 - 스트림 파이프라인에서 요소를 처리할 때 peek 메서드로 중간값을 확인할 수 있다.

추가 참고 자료
 - https://github.com/ckddn9496/modern-java-in-action/blob/main/contents/Chapter%209%20-%20%EB%A6%AC%ED%8C%A9%ED%84%B0%EB%A7%81%2C%20%ED%85%8C%EC%8A%A4%ED%8C%85%2C%20%EB%94%94%EB%B2%84%EA%B9%85.md