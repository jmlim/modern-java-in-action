# 6. 스트림으로 데이터 수집
이 장에서는 reduce 가 그랬던 것 처럼 collect 역시 다양한 요소 누적 방식을 인수로 받아서 스트림을 최종 결과로 도출할 수 있는 리듀싱 연산을 수행할 수 있음을 설명한다. 

~~~java

public static List<Transaction> transactions = Arrays.asList(
        new Transaction(Currency.EUR, 1500.0),
        new Transaction(Currency.USD, 2300.0),
        new Transaction(Currency.GBP, 9900.0),
        new Transaction(Currency.EUR, 1100.0),
        new Transaction(Currency.JPY, 7800.0),
        new Transaction(Currency.CHF, 6700.0),
        new Transaction(Currency.EUR, 5600.0),
        new Transaction(Currency.USD, 4500.0),
        new Transaction(Currency.CHF, 3400.0),
        new Transaction(Currency.GBP, 3200.0),
        new Transaction(Currency.USD, 4600.0),
        new Transaction(Currency.JPY, 5700.0),
        new Transaction(Currency.EUR, 6800.0)
        );

...

Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();
for (Transaction transaction : transactions) {
    Currency currency = transaction.getCurrency();
    List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);
    if (transactionsForCurrency == null) { // 현재 통화를 그룹화하는 맵에 항목이 없으면 항목을 만듬
        transactionsForCurrency = new ArrayList<>();
        transactionsByCurrencies.put(currency, transactionsForCurrency);
    }
    transactionsForCurrency.add(transaction); // 같은 통화를 가진 트랜잭션 리스트에 현재 탐색 중인 트랜잭션을 추가한다. 
}

System.out.println(transactionsByCurrencies);
~~~

 - 매우 간결해진다.
~~~java
Map<Currency, List<Transaction> transactionsByCurrencies = transactions.stream().collect(groupingBy(Transaction::getCurrency));
~~~

## 6.1. 컬렉터란 무엇인가?
 - 이전 예제에서 collect 메서드로 Collector 인터페이스 구현을 전달했다. 
 - Collector 인터페이스 구현은 스트림의 요소를 어떤식으로 도출할지 지정한다. 
   - groupingBy를 이용해서 '각 키 (통화) 버킷 그리고 각 키 버킷에 대응하는 요소 리스트를 값으로 포함하는 맵을 만들라' 는 동작을 수행한다.
 - 다수준으로 그룹화를 수행할 때 명령형 프로그래밍과 함수형 프로그래밍의 차이점이 더욱 두드러짐.
   - 명령형 코드에서는 문제를 해결하는 과정에서 다중 루프와 조건문을 추가하며 가독성과 유지보수성이 떨어짐.
   - 함수형 프로그래밍에서는 필요한 컬렉터를 쉽게 추가할 수 있다.
### 6.1.1. 고급 리듀싱 기능을 수행하는 컬렉터
 - 훌륭하게 설계된 함수형 API 의 또 다른 정점으로 높은 수준의 조합성과 재사용성을 꼽을 수 있다.
 - collect 로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이 컬렉터의 최대 강점.
 - 통화 예제에서 보여주는 것 처럼 Collector 인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림에 어떤 리듀싱 연산을 수행할지 결정된다.
   - 커스텀 컬렉터를 구현할 수도 있고 Collectors 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다. 
   - 가장 많이 사용하는 직관적인 정적메서드로 toList를 꼽을 수 있다.

### 6.2.2. 미리 정의된 컬렉터
 - Collectors에서 제공하는 메서드의 기능은 크게 세 가지로 구분할 수 있다.
   - 스트림 요소를 하나의 값으로 리듀스하고 요약
   - 요소 그룹화 
   - 요소 분할

## 6.2. 리듀싱과 요약
 - 이미 배웠듯이 컬렉터(Stream.collect 메서드의 인수) 로 스트림의 항목을 컬렉션으로 재구성 할 수 있다.
   - 좀 더 일반적으로 말해 컬렉터로 스트림의 모든 항목을 하나의 결과로 합칠 수 있다.

 - 첫번째 예제 
   - counting() 이라는 팩토리 메서드가 반환하는 컬렉터로 메뉴에서 요리수를 계산
~~~java
long howManyDishes = menu.stream().collect(Collectors.counting())

// 다음처럼 불필요한 과정을 생략할 수 있다.
long howManyDishes = menu.stream().count();
~~~

### 6.2.1. 스트림값에서 최댓값과 최솟값 검색
 - 메뉴에서 칼로리가 가장 높은 요리를 찾는다고 가정.
   - Collectors.maxBy, Collectors.minBy 두 개의 메서드를 이용해서 스트림의 최댓값과 최솟값을 계산할 수 있다.
   - 두 컬렉터는 스트림의 요소를 비교하는 데 사용할 Comparator 를 인수로 받는다.
   - 아래는 칼로리로 요리를 비교하는 Comparator 를 구현한 다음에 Collectors.maxBy 로 전달하는 코드다.
~~~java
Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));
~~~

 - 스트림에 있는 객체의 숫자 필드의 합계나 평균 등을 반환하는 연산에도 리듀싱 기능이 자주 사용된다.
   - 이러한 연산을 요약(summarization) 연산이라 부른다.

### 6.2.2. 요약 연산
- Collectors 클래스는 Collectors.summingInt 라는 특별한 요약 팩토리 메서드를 제공.
- summingInt 의 인수로 전달된 함수는 객체를 int 로 매핑한 컬렉터 반환
- 다음은 메뉴 리스트의 총 칼로리를 계산하는 코드
~~~java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
~~~
 - 칼로리로 매핑된 각 요리의 값을 탐색하면서 초깃값(여기서는 0) 으로 설정되어 있는 누적자에 칼로리를 더한다.

 - Collectors.summingInt, summingDouble 메서드는 같은 방식으로 동작하며 각각 long 또는 double 형식의 데이터로 요약한다는 점만 다름.
 - 단순 합계 외에 평균값 계산 등의 연산도 요약 기능으로 제공 (averagingInt, averagingLong 등)
 - 종종 이들 중 두개 이상의 연산을 한 번에 수행해야 할 때도 있다. 이런 상황에서 팩토리 메서드 summarizingInt 가 반환하는 컬렉터를 사용할 수 있다.
   - 아래 코드를 실행하면 IntSummaryStatistics  클래스로 모든 정보가 수집된다.
~~~java
IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
System.out.println(menuStatistics);
~~~
실행결과
~~~java
IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}
~~~
마찬가지로 int 뿐 아니라 long 이나 double 에 대응하는 메서드 및 클래스도 존재

### 6.2.3. 문자열 연결
 - 컬렉터에 joining 팩토리 메서드를 이용하면 스트림의 각 객체에 toString 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환
~~~java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
System.out.println(shortMenu);

// 요리명 리스트를 콤마로 구분 가능
shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
System.out.println(shortMenu);
~~~
### 6.2.4. 범용 리듀싱 요약 연산
 - 지금까지 살펴본 모든 컬렉터는 reducing 팩토리 메서드로도 정의할 수 있다.
   - 즉, 볌용 Collectors.reducing 으로도 구현할 수 있다.
   - 그럼에도 이전 예제에서 범용 팩토리 메서드 대신 특화된 컬렉터를 사용한 이유는 편의성 때문이다.
   - 다음 코드처럼 reducing 메서드로 만들어진 컬렉터로도 메뉴의 모든 칼로리 합계를 계산할 수 있다.

~~~java
int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (a, b) -> a + b));
System.out.println(totalCalories);
~~~
 - 첫 번째 인수는 리듀싱 연산의 시작값이거나 스트림에 인수가 없을 때는 반환값이다. 
 - 두 번째 인수는 변환 함수
 - 세 번째 인수는 같은 종류의 두 항목을 하나의 값으로 더하는 BinaryOperator 다. 예제에서는 두 개의 int 가 사용


 - 다음처럼 한 개의 인수를 가진 reducing 버전을 이용해서 가장 칼로리가 높은 요리를 찾는 방법도 있다.
~~~java
  Optional<Dish> mostCalorieDish1 = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
  System.out.println(mostCalorieDish1);
~~~

#### 컬렉션 프레임워크 유연성
reducing 컬렉터를 사용한 이전 예제에서 람다 표현식 대신 Integer::sum 메서드 참조를 이용하면 코드를 좀 더 단순화 할 수 있다.
~~~java
int totalCalories = menu.stream().collect(reducing(0, 
        Dish::getCalories, //합계 함수 
        Integer::sum)); // 변환 함수
~~~

counting 컬렉터도 세 개의 인수를 갖는 reducing 팩토리 메서드를 이용해서 구현할 수 있다.
 - 즉 다음 코드처럼 스트림의 Long 객체 형식의 요소를 1로 변환한 다음에 모두 더할 수 있다.
~~~java
public static <T> Collector<T, ?, Long> counting() {
    return reducing(0L, e -> 1L, Long::sum);    
}
~~~
 - 위 예제에서 두 번째 제네릭 형식으로 와일드 카드 '?' 사용
   - ? 는 컬렉터의 누적자 형식이 알려지지 않았음을, 즉 누적자의 형식이 자유로움을 의미한다. 
   

- 스트림을 IntStream으로 매핑한 다음에 sum 메서드를 호출하는 방법으로도 결과를 얻을 수 있음.
~~~java
int totalCalories = menu.stream().mapToInt(Dish::getCalories).sum();
~~~

#### 자신의 상황에 맞는 최적의 해법 선택
 - 지금까지 살펴본 예제는 함수형 프로그래밍(특히 자바8의 컬렉션 프레임워크에 추가된 함수형 원칙에 기반한 새로운 API) 에서는 하나의 연산을 다양한 방법으로 해결할 수 있음을 보여준다.
 - 또한 스트림 인터페이스에서 직접 제공하는 메서드를 이용하는 것에 비해 컬렉터를 이용하는 코드가 더 복잡하다는 사실도 보여준다.
   - 코드가 좀 더 복잡한 대신 재사용성과 커스터마이즈 가능성을 제공하는 높은 수준의 추상화와 일반화를 얻을 수 있다.
 - 문제를 해결할 수 있는 다양한 해결 방법을 확인한 다음에 가장 일반적으로 문제에 특화된 해결책을 고르는 것이 바람직
   - ex) 메뉴의 전체 칼로리를 계산하는 예제에서는 가장 마지막에 확인한 해결 방법이 가독성이 가장 좋고 간결하다. (IntStream 사용한..)
   - 또한 IntStream 덕분에 자동 언박싱 연산을 수행하거나 Integer 를 int 로 변환하는 과정을 피할 수 있으므로 성능까지 좋다.


## 6.3. 그룹화
 - 트랜잭션 통화 그룹화 예제에서 확인했듯이 명령형으로 그룹화를 구현하려면 까다롭고, 할일이 많으며 에러도 많이 발생한다.
   - 자바 8의 함수형을 이용하면 가독성 있는 한 줄의 코드로 그룹화를 구현할 수 있다.
   
다음 처럼 팩토리 메서드 Collectors.groupingBy를 이용해서 쉽게 메뉴를 그룹화 할 수 있다.
~~~java
Map<Dish.Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
~~~

결과
~~~java
{FISH=[prawns, salmon], OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork, beef, chicken]}
~~~
 - 스트림의 각 요리에서 Dish.Type과 일치하는 모든 요리를 추출하는 함수를 groupingBy  메서드로 전달했다.
   - 이 함수를 기준으로 스트림이 그룹화되므로 이를 분류 함수(classification function) 라고 부른다.

 - 400 칼로리 이하를 'diet'로, 400~700 칼로리를 'normal' 700 칼로리 초과를 'fat' 요리로 분류하기.
~~~java
 public enum CaloricLevel {DIET, NORMAL, FAT};
 public static void main(String[] args) {
     Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
             groupingBy(dish -> {
                 if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                 else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                 else return CaloricLevel.FAT;
             })
     );
 }
~~~

### 6.3.1. 그룹화된 요소 조작
 - 500 칼로리가 넘는 요리만 필터 후 type 으로 그룹화
~~~java
Map<Dish.Type, List<Dish>> caloricDishesByType = menu.stream().filter(d -> d.getCalories() > 500)
       .collect(groupingBy(Dish::getType));
System.out.println(caloricDishesByType);
~~~
결과
~~~java
{MEAT=[pork, beef], OTHER=[french fries, pizza]}
~~~
 - 위 예제의 문제는 핕터 프레디케이트를 만족하는 FISH 종류가 없으므로 결과 맵에 해당 키 자체가 포함이 되지 않음.
 - Collectors 클래스는 일반적인 분류 함수에 Collector 형식의 두 번째 인수를 갖도록 groupingBy 팩토리 메서드를 오버로드해 이 문제를 해결한다.
   - 두 번째 Collector 안으로 필터 프레디케이트를 이동하여 해결한다.
~~~java
Map<Dish.Type, List<Dish>> caloricDishesByType2 = menu.stream()
        .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));

System.out.println(caloricDishesByType2);
~~~
결과
~~~java
{MEAT=[pork, beef], FISH=[], OTHER=[french fries, pizza]}
~~~

 - 그룹화된 항목을 조작하는 다른 유용한 기능 중 또 다른 하나로 맵핑 함수를 이용해 요소를 변환하는 작업이 있다.
   - filtering 컬렉터와 같은 이유로 Collectors 클래스는 매핑 함수와 각 항목에 적용한 함수를 모으는 데 사용하는 또 다른 컬렉터를 인수로 받는 mapping 메서드를 제공한다.
   - 예를 들어 이 함수를 이용해 그룹의 각 요리를 관련 이름 목록으로 반환할 수 있다.
~~~java
Map<Dish.Type, List<String>> dishNamesByType = menu.stream()
       .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));

System.out.println(dishNamesByType);
~~~
결과
~~~java
{MEAT=[pork, beef, chicken], FISH=[prawns, salmon], OTHER=[french fries, rice, season fruit, pizza]}
~~~
 - 맵의 각 그룹은 요리(Dish) 가 아닌 문자열 리스트이다.


 - 또한 groupingBy와 연계해 세 번째 컬렉터를 사용해서 일반 맵이 아닌 flatMap 변환을 수행할 수 있다.
 - 다음처럼 태그 목록을 가진 각 요리로 구성된 맵이 있다고 가정

~~~java
Map<String, List<String>> dishTags = new HashMap<>();

dishTags.put("pork", asList("greasy", "salty"));
dishTags.put("beef", asList("salty", "roasted"));
dishTags.put("chicken", asList("fried", "crisp"));
dishTags.put("french fries", asList("greasy", "fried"));
dishTags.put("rice", asList("light", "natural"));
dishTags.put("season fruit", asList("fresh", "natural"));
dishTags.put("pizza", asList("tasty", "salty"));
dishTags.put("prawns", asList("tasty", "roasted"));
dishTags.put("salmon", asList("delicious", "fresh"));
~~~

flatMapping 컬렉터를 이용하면 각 형식의 요리의 태그를 간편하게 추출할 수 있다.

~~~java
Map<Dish.Type, Set<String>> dishNamesByType2 = menu.stream().collect(
       groupingBy(Dish::getType,
               flatMapping(dish -> dishTags.get(dish.getName()).stream()
                       , toSet()))
);
System.out.println(dishNamesByType2);
~~~

결과
~~~java
{MEAT=[salty, greasy, roasted, fried, crisp], FISH=[roasted, tasty, fresh, delicious], OTHER=[salty, greasy, natural, light, tasty, fresh, fried]}
~~~

- 각 요리에서 태그 리스트를 얻고 두 수준의 리스트를 한 수준으로 평면화하려면 flatMap 을 수행해야 한다.
- 이전처럼 각 그룹에 수행한 flatMapping 연산 결과를 수집해서 리스트가 아니라 집합으로 그룹화해 중복 태그를 제거한다.

### 6.3.2. 다수준 그룹화
 - 두 인수를 받는 팩토리 매서드 Collectors.groupingBy를 이용해서 항목을 다수준으로 그룹화 할 수 있다.
   - Collectors.groupingBy는 일반적인 분류 함수와 컬렉터를 인수로 받음.
~~~java
// 다수준 그룹화
Map<Dish.Type, Map<CaloricLevel, List<Dish>>> disheByTypeCaloricLevel = menu.stream().collect(
       groupingBy(Dish::getType, // 첫 번째 수준의 분류 함수
               groupingBy(dish -> { // 두 번째 수준의 분류 함수
                   if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                   else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                   else return CaloricLevel.FAT;
               }))
);
System.out.println(disheByTypeCaloricLevel);
~~~
결과
~~~java
{MEAT={DIET=[chicken], NORMAL=[beef], FAT=[pork]},
FISH={DIET=[prawns], NORMAL=[salmon]}, 
OTHER={DIET=[rice, season fruit], NORMAL=[french fries, pizza]}}
~~~
### 6.3.3. 서브그룹 데이터 수집
 - groupingBy 로 넘겨주는 컬렉터의 형식은 제한이 없음.
 - 다음 코드처럼 groupingBy 컬렉터에 두 번째 인수로 counting 컬렉터를 전달해서 메뉴에서 요리의 수를 종류별로 계산할 수 있다.

~~~java
// 6.3.3 서브그룹으로 데이터 수집
Map<Dish.Type, Long> typesCount = menu.stream()
       .collect(
               groupingBy(Dish::getType, counting())
       );
System.out.println(typesCount);
~~~
결과
~~~java
{MEAT=3, FISH=2, OTHER=4}
~~~

- 메뉴에서 가장 높은 칼로리를 가진 요리를 찾는 프로그램 구현
~~~java
Map<Dish.Type, Optional<Dish>> mostCaloricByType = menu.stream().collect(
       groupingBy(Dish::getType,
               maxBy(comparingInt(Dish::getCalories)))
);
System.out.println(mostCaloricByType);
~~~

결과
~~~java
{MEAT=Optional[pork], FISH=Optional[salmon], OTHER=Optional[pizza]}
~~~

#### 컬렉터 결과를 다른 형식에 적용하기
 - 마지막 그룹화 연산에서 맵의 모든 값을 Optional 로 감쌀 필요가 없으므로 Optional 을 삭제할 수 있다.
   - 즉 다음처럼 팩토리 메서드 Collectors.collectingAndThen 으로 컬렉터가 반환된 결과를 다른 형식으로 활용할 수 있다.
~~~java
// 각 서브그룹에서 가장 칼로리가 높은 요리 찾기.
Map<Dish.Type, Dish> mostCaloricByType2 = menu.stream()
     .collect(groupingBy(Dish::getType, // 분류함수
        collectingAndThen(maxBy(comparingInt(Dish::getCalories)), // 감싸인 컬렉터
        Optional::get // 변환함수
        )
     ));
  System.out.println(mostCaloricByType2);
~~~
결과
~~~java
{MEAT=pork, FISH=salmon, OTHER=pizza}
~~~
 - collectingAndThen 은 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다.
 - 이 예제에서는 maxBy로 만들어진 컬렉터가 감싸지는 컬렉터며 변환함수 Optional::get 으로 반환된 Optional 에 포함된 값을 추출한다.

가장 외부 계층에서 안쪽으로 다음와 같은 작업이 수행된다. 
 - 요리의 종류에 따라 메뉴 스트림을 세 개의 서브스트림으로 그룹화
 - groupingBy 컬렉터는 collectingAndThen 컬렉터를 감싼다. 따라서 두 번째 컬렉터는 그룹화된 세 개의 서브스트림에 적용된다.
 - collectingAndThen 컬렉터는 세 번째 컬렉터 maxBy를 감싼다.
 - 리듀싱 컬렉터가 서브스트림에 연산을 수행한 결과에 collectingAndThen 의 Optional::get 변환 함수가 적용된다.
 - groupingBy 컬렉터가 반환하는 맵의 분류 키에 대응하는 세 값이 각각의 요리 형식에서 가장 높은 칼로리다.

#### groupingBy 와 함께 사용하는 다른 컬렉터 예제
 - 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때는 팩터리 메서드 groupingBy 에 두 번째 인수로 전달한 컬렉터를 사용
 - 메뉴에 있는 모든 요리의 칼로리 합계를 구하려고 만든 컬렉터를 재사용 할 수 있다.
~~~java
 Map<Dish.Type, Integer> totalCaloriesByType = menu.stream().collect(
       groupingBy(Dish::getType,
               summingInt(Dish::getCalories))
);
System.out.println(totalCaloriesByType);
~~~
결과
~~~java
{MEAT=1900, FISH=850, OTHER=1550}
~~~


 - 이 외에도 mapping 메서드로 만들어진 컬렉터도 groupingBy 와 자주 사용된다.
 - 각 요리 형식에 존재하는 모든 CaloricLevel 값을 알고 싶다고 가정할 때
~~~java
//각 요리 형식에 존재하는 모든 CaloricLevel 값을 알고 싶다고 가정할 때
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream().collect(
       groupingBy(Dish::getType,
               mapping(dish -> {
                   if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                   else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                   else return CaloricLevel.FAT;
               }, toSet()))
);
System.out.println(caloricLevelsByType);
~~~
결과
~~~java
{MEAT=[DIET, FAT, NORMAL], FISH=[DIET, NORMAL], OTHER=[DIET, NORMAL]}
~~~

~~~java
  // toCollection을 이용하면 원하는 방식으로 결과를 제어할 수 있다.
  Map<Dish.Type, HashSet<CaloricLevel>> caloricLevelsByType2 = menu.stream().collect(
          groupingBy(Dish::getType,
                  mapping(dish -> {
                      if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                      else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                      else return CaloricLevel.FAT;
                  }, toCollection(HashSet::new)))
  );
  System.out.println(caloricLevelsByType2);
~~~
## 6.4. 분할
분할은 분할 참수(partitioning function) 라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다.
분할 함수는 불리언을 반환하므로 맵의 키 행식은 Boolean 이다. 결과적으로 그룹화 맵은 최대(참 아니면 거짓의 값을 갖는) 두 개의 그룹으로 분류된다. 

모든 요리를 채식 요리와 채직이 아닌 요리로 분류한다고 가정.
~~~java
Map<Boolean, List<Dish>> partitionedMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian)); // 분할 함수
~~~

결과
~~~java
{false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit, pizza]}
~~~

참값의 키로 맵에서 모든 채식 요리를 얻을 수 있다. 
~~~java
List<Dish> vegeratianDishes =   partitionedMenu.get(true)
~~~

### 6.4.1. 분할의 장점
 - 분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점.
 - 이전 코드를 활용하면 채식 요리와 채식의 아닌 요리 각각의 그룹에서 가장 칼로리가 높은 요리도 찾을 수 있다.

~~~java
// 채식 요리 와 채식이 아닌 요리 각각의 그룹에서 칼로리가 가장 높은 요리 찾기.
Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream()
       .collect(partitioningBy(Dish::isVegetarian, collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));
System.out.println(mostCaloricPartitionedByVegetarian);
~~~
결과
~~~java
{false=pork, true=pizza}
~~~
### 6.4.2. 숫자를 소수와 비소수로 분할하기
- 정수 n을 인수로 받아서 2에서 n 까지의 자연수를 소수와 비소수로 나누는 프로그램 구현하기.
- 먼저 주어진 수가 소수인지 아닌지 판단하는 프레디케이트를 구현
~~~java
public boolean isPrime(int candidate) {
    return IntStream.range(2, candidate)
        .noneMatch(i -> candidate % i == 0); // 스트림의 모든 정수로 candidate를 나눌 수 없으면 참을 반환 
}
~~~

다음처럼 소수의 대상을 주어진 수의 제곱근 이하의 수로 제한할 수 있다.
~~~java
public boolean isPrime(int candidate) {
    int candidateRoot = (int) Math.sqrt((double) candidate);
    return IntStream.rangeClosed(2, candidateRoot)
        .noneMatch(i -> candidate % i == 0);
}
~~~
이제 n 개의 숫자를 포함하는 스트림을 만든 다음에 우리가 구현한 isPrime 메서드를 프레디케이트로 이용하고 partitioningBy컬렉터로 리듀스해서 숫자를 소수와 비소수로 분류할 수 있다.
~~~java
  public Map<Boolean, List<Integer>> partitionPrimes(int n) {
        return IntStream.rangeClosed(2, n).boxed()
                .collect(partitioningBy(this::isPrime)); // partitioningBy(candidate -> isPrime(candidate)) 
    }
~~~
> Collectors 클래스의 정적 팩토리 메서드

> toList, toSet, toCollection, counting, summingInt, averagingInt, summarizingInt, joining, maxBy, minBy, reducing, collectingAndThen, groupingBy, partitioningBy 등

## 6.5. Collector 인터페이스
Collector 인터페이스는 리듀싱 연산(즉, 컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다. 지금까지 toList 나 groupingBy 등 Collector 인터페이스를 구현하는 많은 컬렉터를 살펴봤다. 
우리가 Collector 인터페이스를 구현하는 리듀싱 연산을 만들 수도 있다. Collector 인터페이스를 직접 구현해서 더 효율적으로 문제를 해결하는 컬렉터를 만드는 방법을 살펴본다.

~~~java
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    BinaryOperator<A> combiner();
    Function<A, R> finisher();
    Set<Charateristics> characteristics();
}
~~~
 - T 는 수집될 항목의 제네릭 형식이다. 
 - A 는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다. 
 - R 은 수집 연산 결과 객체의 형식이다. 

얘를 들어 Stream의 모든 요소를 `List<T>`로 수집하는 ToListCollector라는 클래스는 아래와 같이 만들 수 있다. 
~~~java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {
   ...
}
~~~

### 6.5.1. Collector 인터페이스의 메서드 살펴보기
 - 먼저 살펴볼 네 개의 메서드는 collect 메서드에서 실행하는 함수를 반환
 - 다섯번째 메서드 characteristics는 collect 메서드가 어떤 최적화(예를 들면 병렬화)를 이용해서 리듀싱 연산을 수행할 것인지 결정하도록 돕는 힌트 특정 집합 제공.

#### supplier 메서드 : 새로운 결과 컨테이너 만들기.
 - supplier 메서드는 수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수
 - ToListCollector에서 supplier는 다음처럼 빈 리스트를 반환

~~~java
public Supplier<List<T>> supplier() {
    return () -> new ArrayList<T>(); // or ArrayList::new    
}
~~~

#### accumulator 메서드 : 결과 컨테이너에 요소 추가하기.
 - accumulator 메서드는 리듀싱 연산을 수행하는 함수를 반환한다. 
 - 스트림에서 n 번째 요소를 탐색할 때 두 인수
   - 즉 누적자(스트림의 첫 n-1개 항목을 수집한 상태)와 n번째 요소를 함수에 적용한다.
~~~java
public BiConsumer<List<T>, T> accumulator() {
    return (list, item) -> list.add(item); // or List::add
}
~~~

#### finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기.
 - finisher 메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼 때 호출할 함수를 반환해야 한다.
 - 때로는 ToListCollector 에서 볼 수 있는 것처럼 누적자 객체가 이미 최종 결과인 상황도 있다. 
 - 이런 때는 변환 과정이 필요하지 않으므로 finisher메서드는 항등 함수를 반환한다. 
~~~java
public  Function<List<T>, Liost<T>> finisher() {
    return Function.identity();
}
~~~

#### combiner 메서드 : 두 결과 컨테이너 병합
 - 네 번째 메서든 combiner 메서드는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다.
   - toList의 combiner 는 비교적 쉽게 구현할 수 있다. 즉, 스트림의 두 번째 서브 파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가하면 된다. 
~~~java
public BinaryOperator<List<T>> combiner() {
    return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
    }
}
~~~

#### characteristics 메서드
 - characteristics 메서드는 컬렉터의 연산을 정의하는 Characteristics 형식의 불변 집합을 반환한다.
~~~java
enum Characteristics {
    CONCURRENT,
    UNORDERED,
    IDENTITY_FINISH
}
~~~
 - UNORDERED : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
 - CONCURRENT : 다중 스레드에서 accumulator 함수를 동시에 호출할 수 있으며 병렬 리듀싱을 수행할 수 있다. 컬렉터의 플래그에 UNORDERED를 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않은 상황에서만 병렬 리듀싱을 수행할 수 있다.
 - IDENTITY_FINISH : finisher 메서드가 반환하는 함수는 단순히 identity를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다. 또한 누적자 A를 결과 R로 안전하게 형변환할 수 있다.

### 6.5.2. 응용하기
- 자신만의 커스텀 ToListCollector 구현
~~~java
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new; // 수집 연산의 시발점
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add; // 탐색한 항목을 누적하고 바로 누적자를 고친다.
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity(); // 항등 함수
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) -> { // 두 번째 콘텐트와 합쳐서 첫 번째 누적자를 고친다.
            list1.addAll(list2); // 변경된 첫 번째 누적자를 반환한다.
            return list1;
        };
    }



    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(
                IDENTITY_FINISH, CONCURRENT));
    }
}
~~~
 - 위 구현이 Collectors.toList 메서드가 반환하는 결과와 완전히 같은 것은 아니지만 사소한 최적화를 제외하면 대체로 비슷한다. 
 - 특히 자바 API에서 제공하는 컬렉터는 싱글턴 Collections.emptyList() 로 빈 리스트를 반환한다. 
 - 이제 자바에서 제공하는 API 대신 우리가 만든 컬렉터를 메뉴 스트림의 모든 료리를 수집하는 예제에 사용할 수 있다.

~~~java
List<Dish> dishes = menuStream.collect(new ToListCollector<Dish>()); // 기존코드 : menuStream.collect(toList()); 
~~~
 - 기존 코드의 toList는 팩토리지만 ToListCollector 는 new 로 인스턴스화한다는 점이 다름.

#### 컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기.

~~~java
// 컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기.
List<Dish> dishes2 = menu.stream().collect(ArrayList::new,
       List::add,
       List::addAll);
System.out.println(dishes2);
~~~
 - 이전 코드에 비해 좀 더 간결하고 축약되어 있지만 가독성은 떨어지므로 적절한 클래스로 커스텀 컬렉터를 구현하는 편이 중복을 피하고 재사용성을 높이는 데 도움이 된다.


## 6.6. 커스텀 컬렉터를 구현해서 성능 개선하기
### 6.6.1. 소수로만 나누기
 - 제수(devisor)를 현재 숫자 이하에서 발견한 소수로 제한할 수 있음.
 - 주어진 숫자가 소수인지 아닌지 판단해야 하는데, 그러려면 지금까지 발견한 소수 리스트에 접근해야 한다.
   - 커스텀 컬렉터로 이 문제를 해결할 수 있다.

중간 결과 리스트가 있다면 isPrime 메서드로 중간 결과 리스트 전달
~~~java
public boolean isPrime(List<Integer> primes, int candidate) {
    return primes.stream().noneMatch(i -> candidate % i == 0);    
}
~~~

다음 소수가 대상의 루트보다 크면 소수로 나누는 검사를 멈춰야 한다. 
~~~java
 public boolean isPrime(List<Integer> primes, int candidate) {
     int candidateRoot = (int) Math.sqrt((double) candidate);
     return primes.stream()
         .takeWhile(i -> i <= candidateRoot)
         .noneMatch(i -> candidate % i == 0);
}
~~~

새로운 isPrime 메서드를 구현했으니 본격적으로 커스텀 컬렉터를 구현하자.
#### 1단계 : Collector 클래스 시그니처 정의
`Collector<T, A, R>`
 - T : 스트림 요소의 형식
 - A : 중간 결과를 누적하는 객체의 형식
 - R : collect 연산의 최종 결과 형식

~~~java

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static io.jmlim.modernjavainaction.chap06.customcollector.Prime.isPrime;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class PrimeNumbersCollector implements Collector<Integer,
        Map<Boolean, List<Integer>>, // 누적자 형식
        Map<Boolean, List<Integer>>> {  // 수집 연산의 결과 형식

   /**
    * 누적자를 만드는 함수 반환
    *
    * @return
    */
   @Override
   public Supplier<Map<Boolean, List<Integer>>> supplier() {
      return () -> new HashMap<>() {{
         put(true, new ArrayList<>());
         put(false, new ArrayList<>());
      }};
   }

   @Override
   public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
      return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
         acc.get(isPrime(acc.get(true), candidate)) // isPrime 의 결과에 따라 소수 리스트와 비소수 리스트를 만듬
                 .add(candidate);  // candidate 를 알맞은 리스트에 추가.
      };
   }

   @Override
   public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
      return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
         map1.get(true).addAll(map2.get(true));
         map1.get(false).addAll(map2.get(false));
         return map1;
      };
   }

   /**
    * 변환과정이 필요 없으므로 항등함수 반환
    *
    * @return
    */
   @Override
   public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
      return Function.identity();
   }

   @Override
   public Set<Characteristics> characteristics() {
      // 발견한 소수의 순서에 의미가 있으므로 IDENITTY_FINISH 지만 UNORDERED , CONCURRENT  는 아니다.
      return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
   }
}
~~~



### 6.6.2. 컬렉터 성능 비교

~~~java
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CollectorHarness {
    public static void main(String[] args) {
        speedTest(PrimeNumbersCollectorExample::partitionPrimes);
        speedTest(PrimeNumbersCollectorExample::partitionPrimesWithCustomCollector);
    }

    private static void speedTest(Function<Integer, Map<Boolean, List<Integer>>> function) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();

            function.apply(1_000_000);

            long duration = (System.nanoTime() - start) / 1_000_000;
            if (duration < fastest) fastest = duration;
        }
        System.out.println("Fastest execution done in " + fastest + " msecs");
    }
}

~~~

결과
~~~java
Fastest execution done in 290 msecs
Fastest execution done in 175 msecs
~~~

## 6.7. 마치며
 - collect는 스트림의 요소를 요약 결과로 누적하는 다양한 방법(컬렉터라 불리는)을 인수로 갖는 최종 연산인다.
 - 스트림의 요소를 하나의 값으로 리듀스하고 요약하는 컬렉터뿐 아니라 최솟값, 최댓값, 평균값을 계산하는 컬렉터 등이 미리 정의되어 있다.
 - 미리 정의된 컬렉터인 groupingBy 로 스트림의 요소를 그룹화하거나, partitioningBy로 스트림의 요소를 분할할 수 있다.
 - 컬렉터는 다수준의 그룹화, 분할, 리듀싱 연산에 적합하게 설계되어 있다.
 - Collector 인터페이스에 정의된 메서드를 구현해서 커스텀 컬렉터를 개발할 수 있다.