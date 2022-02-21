# 11. null 대신 Optional 클래스
## 11.1. 값이 없는 상황을 어떻게 처리할까?
~~~java
public String getCarInsuranceName(Person person) {
    return person.getCar().getInsurance().getName();    
}
~~~
 - 차를 소유하지 않은 사람도 많다. 또한 person 이 null 일 수도 있다.

### 11.1.1. 보수적인 자세로 NullPointerException 줄이기

 - 깊은 의심
   - nul 확인 코드 때문에 나머지 호출 체인의 들여쓰기 수준이 증가해버림
~~~java
 /**
 * null 안전시도 1: 깊은 의심
 */
private static String getCarInsuranceName1(Person person) {
    if (person != null) {
        Car car = person.getCar();
        if (car != null) {
            Insurance insurance = car.getInsurance();
            if (insurance != null) {
                return insurance.getName();
            }
        }
    }
    return "Unknown";
}
~~~

 - 너무 많은 출구
   - null 확인 코드마다 출구가 생긴다. 
     - 출구가 너무 많으면 유지보수가 어려워짐
~~~java
private static String getCarInsuranceName2(Person person) {
    if (person == null) {
        return "Unknown";
    }

    Car car = person.getCar();
    if (car == null) {
        return "Unknown";
    }

    Insurance insurance = car.getInsurance();
    if (insurance != null) {
        return "Unknown";
    }
    return insurance.getName();
}
~~~

### 11.1.2. null 때문에 발생하는 문제
 - 에러의 근원이다 : NPE는 자바에서 가장 흔하게 발생하는 에러이다.
 - 코드를 어지럽힌다 : 때로는 중첩된 null 확인 코드를 추가해야 하므로 null 때문에 코드 가독성이 떨어진다.
 - 아무 의미가 없다 : null 은 아무 의미도 표현하지 않는다. 특히 정적 형식 언어에서 값이 없음을 표현하는 방법으로는 적절하지 않다.
 - 자바 철학에 위배된다 : 자바는 개발자로부터 모든 포인터를 숨겼다. 하지만 null 포인터는 예외이다.
 - 형식 시스템에 구멍을 만든다 : null 은 무형식이며 정보를 초함하고 있지 않으므로 모든 참조 형식에 null 을 할당할 수 있다.
   - 이런식으로 null 이 할당되기 시작하면서 시스템의 다른 부분으로 null 이 퍼졌을 때 애초에 null 이 어떤 의미로 사용되었는지 알수 없다.
### 11.1.3. 다른 언어는 null 대신 무얼 사용하나?
    - 그루비 같은 언어에서는 안전 내비게이션 연산자(safe navigation operator) (?.) 을 도입해서 null 문제를 해결햇다.
~~~groovy
def carInsuranceName = person?.car?.insurance?.name
~~~

## 11.2. Optional 클래스 소개
 - 자바 8은 하스켈과 스칼라의 영향을 받아서 `java.util.Optional<T>`라는 새로운 클래스를 제공한다.
 - 값이 있으면 Optional 클래스는 값을 감싼다. 반면 값이 없으면 Optional.empty 메서드로 Optional 을 반환한다.


 - Optional로 Persion/Car/Insurance 데이터 모델 재 정의 예제
~~~java
public class Person {
    private Optional<Car> car; // 사람이 차를 소유했을 수도 소유하지 않았을 수도 있으므로 Optional 으로 정의
    
    public Optional<Car> getCar() {
        return car;
    }
}
~~~

~~~java
public class Car {
    private Optional<Insurance> insurance; // 자동차가 보험에 가입되었을수도 있고 아닐수도 있음.

    public Optional<Insurance> getInsurance() {
        return insurance;
    }
}

~~~

~~~java
public class Insurance {
    private String name; // 보험회사에는 반드시 이름이 있다.

    public String getName() {
        return name;
    }
}

~~~
- Optional 클래스를 사용하면서 모델의 의미가 더 명확해졌음을 확인할 수 있다.
  - 사람은 `Optional<Car>` 를 참조하며 자동차는 `Optional<Insurance>` 을 참조하는데, 이는 사람이 자동차를 소유했을 수도 있으며, 자동차는 보험에 가입되어 있을 수도 아닐 수도 있음을 명확히 설명한다.
  - 또한 보험회사 이름은 `Optional<String>` 이 아니라 String 형식으로 선언되어 있는데 이는 보험회사는 반드시 이름을 가져야 함을 보여준다.
- Optional 이 등장하면서 이를 언랩해서 값이 없을 수 있는 상황에 적절하게 대응하도록 강제하는 효과가 있다.

## 11.3. Optional 적용 패턴
### 11.3.1. Optional 객체 만들기
#### 빈 Optional 
 - 정적 팩토리 메서드 Optional.empty 로 빈 Optional 객체를 얻을 수 있다.
 ~~~java
Optional<Car> optCar = Optional.empty()
~~~

#### null 이 아닌 값으로 Optional 만들기 
~~~java
Optional<Car> optCar = Optional.of(car);
~~~
 - car 가 null 이라면 즉시 NPE 발생(Optional을 사용하지 않았다면 car의 프로퍼티에 접근하려 할 때 에러가 발생했을 것.)
#### null 값으로 Optional 만들기
  - 정적 팩터리 메서드 Optional.ofNullable 로 null 값을 저장할 수 있는 Optional 을 만들 수 있다.
~~~java
Optional<Car> optCar = Optional.ofNullable(car);
~~~

 - car 가 null 이면 빈 Optional 객체가 반환된다.

### 11.3.2. 맵으로 Optional의 값을 추출하고 변환하기
 - 보험회사의 이름을 추출한다고 가정할 때 다음 코드처럼 이름 정보에 접근하기 전에 insurance 가 null 인지 확인해야 한다.
~~~java
String name = null;
if(insurance  != null) {
    name = insurance.getName();
}
~~~
 - 이런 유형의 패턴에 사용할 수 있도록 Optional 은 map 메서드를 지원한다.
~~~java
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
~~~
 - 스트림의 map과 비슷하게 각 요소에 제공된 함수를 적용한다. 
 - 대신 Optional은 최대 요소의 수가 한 개 이하인 데이터 컬렉션이라 생각하자. 
 - Optional이 비어있으면 아무 일도 일어나지 않는다.
   - 비어있다면 name은 Optional.empty와 동일하다. 
   - map을 통해 반환되는 객체 또한 Optional에 감싸져 반환된다 (Optional).

### 11.3.3. flatMap 으로 Optional 객체 연결
 - map 을 이용해서 아래 코드를 재구현 해보자.
~~~java
public String getCarInsuranceName(Person person) {
    return person.getCar().getInsurance().getName();    
}
~~~
~~~java
Optional<Person> optPerson = Optional.of(person);
// 컴파일 되지 않음!
Optional<String> name = optPerson.map(Person::getCar)
    .map(Car::getInsurance)
    .map(Insurance::getName);
~~~
 - 위 코드는 컴파일 되지 않는다. 변수 optPerson의 형식은 `Optional<Person>` 이므로 map 메서드를 호출할 수 있지만 getCar는 `Optional<Car>` 형식의 객체를 반환한다.
   - 즉 map 연산의 결과는 `Optional<Optional<Car>>` 형식의 객체다.
   - getInsurance 는 또 다른 Optional 객체를 반환하므로 getInsurance 메서드를 지원하지 않는다. 
   - 이 문제는 flatMap 메서드로 해결한다.
     - flatMap은 인수로 받은 함수를 적용해서 생성된 각각의 스트림에서 콘텐츠만 남긴다.
   - 이차원 Optional 을 일차원 Optional 로 평준화해야 한다.
#### Optional 로 자동차의 보험회사 이름 찾기
   - map과 flatMap을 살펴봤으니 사용해보자.

~~~java
public String getCarInsuranceName(Optional<Person> person) {
    return person.flatMap(Person::getCar)
        .flatMap(Car::getInsurance)
        .map(Insurance::getName)
        .orElse("Unknown");
}
~~~
#### 도메인 모델에 Optional을 사용했을 때 데이터를 직렬화할 수 없는 이유
Optional 클래스는 필드 형식으로 사용할 것을 가정하지 않았으므로 Serializable 인터페이스를 구현하지 않았다. 따라서 도메인 모델에 Optional을 사용한다면 직렬화 모델을 사용하는 도구나 프레임워크에서 문제가 생길 수 있다. 만약 직렬화 모델이 필요하다면 변수는 일반 객체로 두되, Optional로 값을 반환받을 수 있는 메서드를 추가하는 방식이 권장된다.
~~~java
public class Person {
    private Car car;
    public Optional<Car> getCarAsOptional() {
        return Optional.ofNullable(car);
    }
}
~~~

### 11.3.4. Optional 스트림 조작
 - 자바 9 에서는 Optinal 을 포함하는 스트림을 쉽게 처리할 수 있도록 Optional 에 stream() 메서드를 추가했다.
 - Optional 스트림을 값을 가진 스트림으로 변환할 때 이 기능을 유용하게 활용할 수 있다.

~~~java
 public Set<String> getCarInsuranceName(List<Person> persons) {
   return persons.stream()
           .map(Person::getCar) // 사람 목록을 각 사람이 보유한 자동차의 Optional<Car> 스트림으로 변환
           .map(optCar -> optCar.flatMap(Car::getInsurance)) // flatMap 연산을 이용해 Optional<Car>을 해당 Optional<Insurance<로 변환
           .map(optIns -> optIns.map(Insurance::getName))// Optional<Insurance>를 해당 이름의 Optional<String> 으로 변환
           .flatMap(Optional::stream) // Stream<Optional<String>> 을 현재 이름을 포함하는 Stream<String> 으로 변환
           .collect(toSet()); // 결과 문자열을 중복되지 않은 값을 갖도록 집합으로 수집
}
~~~

### 11.3.5. 디폴트 액션과  Optional 언랩
- get() 은 값을 읽는 가장 간단한 메서드면서 동시에 가장 안전하지 않은 메서드
    - 값이 없으면 NoSuchElementException 발생
- orElse 메서드를 이용하면 Optional 이 값을 포함하지 않을 때 기본값을 제공할 수 있다.
- orElseGet(Supplier<? extends T> other) : orElse 메서드에 대응하는 게으른 버전의 메서드이다. Optional에 값이 없을 때만 Supplier가 실행된다.
- orElseThrow(Supplier<? extends X> exceptionSupplier) : Optional이 비어있을 때 예외를 발생시킬 수 있으며, 발생시킬 예외의 종류를 정할 수 있다.
- ifPresent(Consumer<? super T> consumer) : 값이 존재할 대 인수로 넘겨준 동작을 실행할 수 있다. 값이 없으면 아무일도 일어나지 않는다.
- (자바 9) ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) : Optional이 비었을 때 실행할 Runnable을 인수로 받는다
### 11.3.6. 두 Optional 합치기
~~~java
  public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
    if (person.isPresent() && car.isPresent()) {
      return Optional.of(findCheapestInsurance(person.get(), car.get()));
    } else {
      return Optional.empty();
    }
  }

  public Insurance findCheapestInsurance(Person person, Car car) {
    // 다양한 보험회사가 제공하는 서비스 조회
    // 모든 결과 데이터 비교
    Insurance cheapestCompany = new Insurance();
    return cheapestCompany;
  }
~~~
 - 두 Optional 을 인수로 받아서 `Optional<Insurance>` 를 반환하는 null 안전 버전의 메서드를 구현해야 한다고 가정. 
   - 인수로 전달한 값 중 하나라도 비어있으면 빈 Optional 을 반환.
     - Optional 클래스는 Optional 이 값을 포함하는지 여부를 알려주는 isPresent 라는 메서드 제공.

 - 위 절에서 배운 map 과 flatMap 메서드를 이용해서 nullSafeFindCheapestInsurance 한줄로 구현하기

~~~java
 public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
  }
~~~
 - 첫 번째 Optional 에 flatMap 을 호출했으므로 첫 번째 Optional 이 비어있다면 인수로 전달한 람다 표현식이 실행되지 않고 그대로 빈 Optional 을 반환
   - 만약 person 값이 있으면 flatMap 메서드에 필요한 `Optional<Insurance>` 를 반환하는 Function의 입력으로 person 을 사용한다. 
   - 이 함수의 바디에서는 두 번째  Optional 에 map을 호출하므로 Optional 이 car값을 포함하지 않으면 Function  은 빈 Optional 을 반환하므로 결국 nullSafeFindCheapestInsurance 는 빈 Optional 을 반환한다. 


### 11.3.7. 필터로 특정값 거르기
~~~java
Insurance insurance = ...; 
if(insurance != null && "CambridgeInsurance".equals(insurance.getName())) {
    System.out.println("ok");
}
~~~

 - 아래와 같이 재 구현 가능
~~~java
Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance ->  "CambridgeInsurance".equals(insurance.getName()))
        .ifPresent(x -> System.out.println("ok"));
~~~

 - filter 메서드는 프레디케이트를 인수로 받는다. Optional 객체가 값을 가지며 프레디케이트와 일치하면 filter 메서드는 그 값을 반환하고 그렇지 않으면 빈 Optional 객체를 반환한다.

~~~java

/**
 * minAge 이상의 나이를 먹은 사람에 대해서만 person의 Insurance Name 값을 리턴하도록 처리 
 * 
 * @param person
 * @param minAge
 * @return
 */
public String getCarInsuranceName(Optional<Person> person, int minAge) {
    return person.filter(p -> p.getAge() >= minAge)
            .flatMap(Person::getCar)
            .flatMap(Car::getInsurance)
            .map(Insurance::getName)
            .orElse("Unknown");
}

~~~


## 11.4. Optional 을 사용한 실용 예제

### 11.4.1. 잠재적으로 null 이 될 수 있는 대상을 Optional 로 감싸기
다음처럼 key 로 값에 접근한다고 가정했을 때
~~~java
Object value = map.get("key")
~~~

 - 기존처럼 if-then-else 또는 아래와 같이 Optional.ofNullable 을 이용하는 방법이 있다. 
~~~java
Optional<Object> value = Optional.ofNullable(map.get("key));
~~~

### 11.4.2. 예외와 Optional 클래스
 - 자바 API 가 어떤 이유에서 값을 제공할 수 없을 때 null 을 반환하는 대신 예외를 발생시킬 때도 있다. 
 - 이것에 대한 전형적인 예가 문제열을 정수로 변환하는 정적 메서드 Integer.parseInt(String) 이다. 
   - 문자열을 정수로 바꾸지 못할 때 NumberFormatException 발생시킴
 - 아래와 같이 빈 Optional 로 해결 가능.
   - 우리도 아래와 같은 메서드를 포함하는 OptionalUtility를 만들기 바란다. 

~~~java
public static Optional<Integer> stringToInt(String s) {
    try {
        return Optional.of(Integer.parseInt(s)); // 문자열을 정수로 변환할 수 있으면 정수로 변환된 값을 포함하는 Optional 을 반환한다.
    } catch (NumberFormatException e) {
        return Optional.empty()    
    }
}
~~~

### 11.4.3. 기본형 Optional 을 사용하지 말아야 하는 이유
 - Optional과 함께 기본형 특화 클래스인 OptionalInt, OptionalLong, OptionalDouble이 존재한다. 
 - 하지만 Optional의 최대 요소 수는 한 개이므로 성능개선이 되지 않는다. 
 - 또한 다른 일반 Optional과 혼용할 수 없으므로 기본형 Optional을 사용하지 않는것을 권장한다.
### 11.4.4. 응용
 - Optional 클래스의 메서드를 실제 업무에서 어떻게 활용할 수 있는지 살펴보기.

예를들어 프로그램의 설정 인수로 Properties 를 전달한다고 가정
~~~java
Properties props = new Properties();
props.setProperty("a", "5");
props.setProperty("b", "true");
props.setProperty("c", "-3");
~~~

프로그램에서는 Properties 를 읽어서 값을 초 단위의 지속시간으로 해석
~~~java
public int readDuration(Properties props, String name)
~~~

지속 시간은 양수여야 하므로 문자열이 양의 정수를 가리키면 해당 정수를 반환하지만 그 외에는 0을 반환한다.
 - 이를 다름처럼 JUnit 어설션으로 구현할 수 있다.
~~~java
 assertEquals(5, readDuration(param, "a"));
 assertEquals(0, readDuration(param, "b"));
 assertEquals(0, readDuration(param, "c"));
 assertEquals(0, readDuration(param, "d"));
~~~

이들 어설션은 프로퍼티 'a'는 양수로 반환할 수 있는 문자열을 포함하므로 readDuration 메서드는 5를 반환
프로퍼티 'b'는 숫자로 변환할 수 없는 문자열을 포함하므로 0을 반환한다. 
프로퍼티 'c'는 음수 문자열을 포함하므로 0을 반환한다. 
'd' 라는 이름의 프로퍼티는 없으므로 0을 반환한다. 

~~~java
public int readDuration(Properties props, String name) {
    String value = props.getProperty(name);
    if(value != null) { 
        try {
            int i = Integer.parseInt(value);
            if(i > 0) {
                return i;    
            } 
        } catch(NumberFormatException nfe) {}    
    }
    return 0;
}
~~~
 - 구현코드가 복잡해졌고 가독성도 나빠짐.
 - 어떻게 개선할 수 있을지 생각해보자.

~~~java
public int readDuration(Properties props, String name){
        return Optional.ofNullable(props.getProperty(name))
        .flatMap(OptionalUtility::stringToInt)
        .filter(i -> i > 0)
        .orElse(0);
}
~~~
## 11.5. 마치며
 - 역사적으로 프로그래밍 언어에서는 null 참조로 값이 없는 상황을 표현해왔다. 
 - 자바 8 에서는 값이 있거나 없음을 표현할 수 있는 클래스 `java.util.Optional<T>` 를 제공한다.
 - 팩토리 매서드 Optional.empty, Optional.of, Optional.ofNullable 등을 이용해서 Optional 객체를 만들 수 있다.
 - Optional 클래스는 스트림과 비슷한 연산을 수행하는 map, flatMap, filter 등의 메서드를 제공한다.
 - Optional 로 값이 없는 상황을 적절하게 처리하도록 강제할 수 있다. 즉, Optional로 예상치 못한 null 예외를 방지할 수 있다.
 - Optional 을 활용하면 더 좋은 API를 설계할 수 있다. 즉, 사용자는 메서드의 시그니처만 보고도 Optional값이 사용되거나 반환되는지 예측할 수 있다.