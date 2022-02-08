# 4. 스트림 소개

## 4.1. 스트림이란 무엇인가?
 - 스트림(Stream)은 자바 8 API에 새로 추가된 기능이다. 
 - 스트림을 이용하면 선언형으로 컬렉션 데이터를 처리할 수 있다. 
 - 멀티스레드 코드를 구현하지 않아도 데이터를 투명하게 병렬로 처리할 수 있다.

저칼로리 요리명을 반환하고, 칼로리를 기준으로 요리를 정렬하는 자바 7코드를 스트림으로 개선
~~~java
    List<Dish> lowCaloricDishes = new ArrayList<>();
        for(Dish dish : menu){
            if(dish.getCalories()< 400){
                lowCaloricDishes.add(dish);
            }
        }
    
    Collections.sort(lowCaloricDishes,new Comparator<Dish>(){
        @Override
        public int compare(Dish dish1,Dish dish2){
             return Integer.compare(dish1.getCalories(),dish2.getCalories());
        }
    });
    
    List<String> lowCaloricDishesName=new ArrayList<>();
    for(Dish dish:lowCaloricDishes){
        lowCaloricDishesName.add(dish.getName());
    }
    
    System.out.println(lowCaloricDishesName);
    }

~~~
- 위 코드에서 lowCaloricDishes 라는 가비지 변수를 사용했다. (컨테이너 역할만 하는 중간변수)
- 자바 8에서는 이러한 세부 구현은 라이브러리 내에서 모두 처리한다.

~~~java
...

    List<String> lowCaloricDishesName = menu.stream().filter(dish->dish.getCalories()< 400)
        .sorted(comparing(Dish::getCalories))
        .map(Dish::getName)
        .collect(toList());
    
    System.out.println(lowCaloricDishesName);
~~~

- stream() -> parallelStream() 으로 바꾸면 이 코드를 멀티코어 아키텍처에서 병렬로 실행할 수 있다.

스트림을 이용할 때의 장점은 아래와 같다.

 - 가비지 변수를 만들지 않는다.
 - 선언형으로 코드를 구현할 수 있다.
 - 여러 빌딩 블록 연산을 연결해서 복잡한 데이터 처리 파이프라인을 만들 수 있다.
 - filter, map, sorted, collect 같은 연산은 고수준 빌딩 블록(high-level building block)으로 이루어져 있으므로 특정 스레딩 모델에 제한되지 않고 자유롭게 어떤 상황에서든 사용할 수 있다.
 
> 스트림 API의 특징은 선언형, 조립할 수 있음, 병렬화로 요약 가능하다.


## 4.2. 스트림 시작하기
 - 스트림이란 "데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소(Sequence of elements)" 로 정의할 수 있다. 


 - 연속된 요소 : 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다. 
   - filter, sorted, map 처럼 표현 계산식이 주를 이룬다. 
 - 소스 : 스트림은 컬렉션, 배열, I/O 자원 등의 데이터 제공 소스로부터 데이터를 소비한다. 
   - 정렬된 컬렉션으로 스트림을 생성하면 정렬이 그대로 유지.
 - 데이터 처리 연산 : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.
   - ex: filter, map, reduce, find, match, sort 등으로 데이터를 조작할 수 있다.
   - 스트림 연산은 순차적으로 또는 병렬로 실행할 수 있다. 

두가지 주요 특징
 - 파이프라이닝(pipelining) : 대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프 라인을 구성할 수 있도록 스트림 자신은 반환한다.
   - 덕분에 게으름(laziness), 쇼트 서킷(short-circuting) 같은 최적화도 언을 수 있다.
 - 내부 반복: 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다. 

아래 소스 다시 보기
~~~java
import static io.jmlim.modernjavainaction.chap04.Dish.menu;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
...

List<String> lowCaloricDishesName = menu.stream()
    .filter(dish->dish.getCalories()< 400) // 400 칼로리 이하 요리 선택
    .sorted(comparing(Dish::getCalories)) // getCalories 기준으로 정렬
    .map(Dish::getName) // 다른 요소로 변환
    .limit(3) // 선착순 3개
    .collect(toList()); // 스트림을 다른 형식으로 변환

System.out.println(lowCaloricDishesName);
~~~

## 4.3. 스트림과 컬렉션
 - 데이터를 언제 계산하느냐가 컬렉션과 스트림의 가장 큰 차이.
   - 컬렉션은 현재 자료구조가 포함하는 모든 값을 메모리에 저장하는 자료구요
   - 스트림은 이론적으로 요청할 때만 요소를 계산하는 고정된 자료구조
     - 스트림에 요소를 추가하거나 스트림에서 요소를 제거할 수 없다.
     - 사용자가 요청하는 값만 스트림에서 추출한다는 것이 핵심.

자바 8의 컬렉션은 DVD에 저장된 영화에 비유하며 스트림은 인터넷으로 스트리밍하는 영화에 비유할 수 있다.
 - DVD 처럼 컬렉션은 현재 자료구조에 포함된 모든 값을 계산한 다음에 컬렉션에 추가할 수 있다.
 - 스트리밍 비디오처럼 필요할 때 값을 계산한다.

### 4.3.1. 딱 한번만 탐색할 수 있다
 - 스트림은 반복자와 마찬가지로 한 번만 탐색할 수 있다. 탐색된 스트림의 요소는 소비된다.
   - 한 번 탐색한 요소를 다시 탐색하려면 초기 데이터 소스에서 새로운 스트림을 만들어야 한다. 

~~~java
List<String> names = Arrays.asList("Java8", "Lambdas", "In", "Action");
Stream<String> s = names.stream();
s.forEach(System.out::println);

//s.forEach(System.out::println); // 스트림이 이미 소비되었거나 닫혀서 illegalStateException 발생
~~~
 - 스트림은 단 한번만 소비할 수 있다는 점을 명심하자.

### 4.3.2. 외부 반복과 내부 반복
 - 컬렉션 인터페이스를 사용하려면 사용자가 직접 요소를 반복해야 한다. 
   - for-each나 Iterator를 이용해서 반복할 수 있다. 이를 외부 반복(external iteration)이라고 한다.
 - 반면 스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림 값을 저장해주는 내부 반복(internal iteration)을 사용한다.
   - 내부 반복의 장점은 병렬성을 쉽게 얻을 수 있다는 점이며, 내부적으로 더 최적화된 방법으로 처리될 수 있기 때문이다.
   - for each 를 이용하는 외부 반복에서는 병렬성을 스스로 관리해야 한다. 
     - 병렬성을 포기하든지 아니면 synchronized 로 시작하는 힘들고 긴 전쟁을 시작해야 한다.

## 4.4. 스트림 연산
 - 스트림 인터페이스는 연산을 크게 두 가지로 구분할 수 있다. 

~~~java
List<String> names = menu.stream()
    .filter(dish->dish.getCalories() > 300) // 중간 연산
    .map(Dish::getName) // 중간연산
    .limit(3) // 중간 연산
    .collect(toList()); // 스트림을 리스트로 변환

System.out.println(lowCaloricDishesName);
~~~

 - filter, map, limit 는 서로 연결되어 파이프라인을 형성.
 - collect 로 파이프라인을 실행한 다음에 닫는다.

 - 스트림은 연결할 수 있는 스트림 연산인 중간 연산(intermediate operation)과 스트림을 닫는 연산인 최종 연산(terminal operation)으로 구성된다.
### 4.4.1. 중간 연산
 - filter 나 sorted 같은 중간 연산은 다른 스트림을 반환한다. 
   - 따라서 여러 중간 연산을 연결해 질의를 만들 수 있다. 
 - 중간 연산의 가장 중요한 특징은 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무도 연산을 수행하지 않는다는 것, 즉 게으르다(lazy)는 것이다. 
 - 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한 번에 처리하기 때문이다.


예제 ) 
~~~java
  List<String> names = menu.stream()
          .filter(dish -> {
              System.out.println("filtering " + dish.getName());
              return dish.getCalories() > 300;
          })
          .map(dish -> {
              System.out.println("mapping " + dish.getName());
              return dish.getName();
          })
          .limit(3)
          .collect(toList());
  System.out.println(names);
~~~

출력 ) 
~~~
filtering pork
mapping pork
filtering beef
mapping beef
filtering chicken
mapping chicken
[pork, beef, chicken]
~~~
 스트림의 게으른 특성 때문에 몇가지 최적의 효과를 얻을 수 있었다.
   - 300 칼로리가 넘는 요리는 여러개지만 오직 처름 3개만 선택됨.
     - 이는 limit 연산 그리고 쇼트서킷이라 불리는 기법 덕분이다. 
   - filter, map 은 서로 다른 연산이지만 한 과정으로 병합되었다.
     - 이 기법을 루프 퓨전이라고 한다.

### 4.4.2. 최종 연산
 - 최종 연산은 스트림 파이프라인에서 결과를 도출한다. 스트림 외의 결과를 반환하는 연산을 말한다.

### 4.4.3. 스트림 이용하기
 - 스트림 이용과정은 세가지로 요약
   - 질의를 수행할(컬렉션 닽은) 데이터 소스
   - 스트림 파이프라인을 구성할 중간 연산 연결
   - 스트림 파이프라인을 실행하고 결과를 만들 최종 연산

## 4.5. 로드맵
 - 5장에서 스트림에서 제공하는 연산을 더 자세히 살펴본다. 
   - 복잡한 데이터 처리 질의를 표현하는 데 사용하는 필터링, 슬라이싱, 검색, 매칭, 매핑, 리듀싱 등 다양한 패턴도 살펴본다.

## 4.6. 마치며
 - 스트림은 소스에서 추출된 연속 요소로, 데이터 처리 연산을 지원한다.
 - 스트림은 내부 반복을 지원한다. 내부 반복은 filter, map, sorted 등의 연산으로 반복을 추상화한다.
 - 스트림은 중간 연산과 최종 연산이 있다.
 - 중간 연산은 filter와 map 처럼 스트림을 반환하면서 다른 연산과 연결되는 연산이다.
   - 중간 연산을 이용해서 파이프라인을 구성할 수 있지만 중간 연산으로는 어떤 결과도 생성할 수 없다.
 - forEach나 count처럼 스트림 파이프라인을 처리해서 스트림이 아닌 결과를 반환하는 연산을 최종 연산이라고 한다.
 - 스트림의 요소는 요청할 때 게으르게 계산된다.