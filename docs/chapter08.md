# 8. 컬렉션 API 개선
 - 자바 8, 9에서 추가된 새로운 컬렉션 API의 기능을 배운다.
## 8.1. 컬렉션 팩토리
> 자바 9에서는 작은 컬렉션 객체를 쉽게 만들 수 있는 몇 가지 방법을 제공한다. 

  - 기존 자바에서는 적은 요소를 포함하는 리스트는 아래와 같이 만듬.
~~~java
List<String> friends = new ArrayList<>();
friends.add("Raphael");
friends.add("Olivia");
friends.add("Thibaut");
~~~

다음처럼 `Arrays.asList()' 팩토리 메서드를 이용하면 코드를 간단하게 줄일 수 있다.
~~~java
List<String> friends = Arrays.asList("Raphael",
        "Olivia",
        "Thibaut")
~~~
 - 고정 크기의 리스트를 만들었으므로 요소를 갱신할 순 있지만 새 요소를 추가하거나 삭제 불가.
   - UnsupportedOperationException 발생.

#### UnsupportedOperationException 예외발생
 - 리스트는 이렇게 팩토리 메서드라고 존재했지만 집합의 경우 리스트를 인수로 받는 HashSet 생성자를 사용하거나 스트림 API를 사용하는 방법이 존재했다.
~~~java
Set<String> friends = new HashSet<>(Arrays.asList("Raphael",
        "Olivia",
        "Thibaut"));

// or
        
Set<String> friends = Stream.of("Raphael",
        "Olivia",
        "Thibaut").collect(toSet());
~~~
- 하지만 두 방법 모두 매끄럽지 못하며 내부적으로 불필요한 객체 할당을 필요로 한다. 그리고 결과는 변환할 수 있는 집합이라는 사실도 주목하자.

### 8.1.1. 리스트 팩토리
 - List.of 팩토리 메서드를 이용해서 간단하게 리스트를 만들 수 있다. 
~~~java
List<String> friends = List.of("Raphael",
        "Olivia",
        "Thibaut");
System.out.println(friends);
~~~
 - 변경할 수 없는 리스트 이므로 추가, 삭제 시 UnsupportedOperationException 발생
 - 데이터 처리 형식을 설정하거나 데이터를 변환할 필요가 없다면 사용하기 간편한 팩토리 메서드를 이용할 것을 권장

### 8.1.2. 집합 팩토리
 - List.of 와 비슷한 방법으로 바꿀 수 없는 집합을 만들 수 있다.
~~~java
Set<String> friends = Set.of("Raphael",
        "Olivia",
        "Thibaut");
System.out.println(friends);
~~~
 - 중복된 요소를 제공해 집합을 만들려고 하면 Olivia 라는 요소가 중복되어 있다는 설명과 함께 IllealArgumentException 발생
 - 집합은 오직 고유의 요소만 포함할 수 있다는 원칙을 상기
~~~java
Set<String> friends = Set.of("Raphael",
        "Olivia",
        "Olivia");
System.out.println(friends);
~~~
결과
~~~
Exception in thread "main" java.lang.IllegalArgumentException: duplicate element: Olivia
~~~

### 8.1.3. 맵 팩토리
 - 자바 9에서는 두 가지 방법으로 바꿀 수 없는 맵을 초기화 가능.


 - 키와 값을 번갈아 제공하는 방법
   - 열개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때는 이 메서드가 유용
~~~java
Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
System.out.println(ageOfFriends);
~~~

 - 그 이상의 맵에서는 Map.Entry<K, V> 객체를 인수로 받으며 가변 인수로 구현된 Map.ofEntries 팩터리 메서드를 이용하는 것이 좋다.
~~~java
Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30),
                entry("Olivia", 25),
                entry("Thibaut", 26)
        );
System.out.println(ageOfFriends);
~~~

## 8.2. 리스트와 집합 처리
 - 자바 8의 List 와 Set 인터페이스에는 다음과 같은 메서드를 추가했다.

#### removeIf
 - 프레디케이트를 만족하는 요소를 제거한다.
     - List, Set 을 구현하거나 해당 구현을 상속받은 모든 클래스에서 이용 가능
#### replaceAll
 - 리스트에서 이용할 수 있는 기능으로  UnaryOperator 함수를 이용해 요소를 바꾼다.

#### sort 
 - List 인터페이스에서 제공하는 기능으로 리스트를 정렬

### 8.2.1. removeIf 메서드

 - 아래 코드는 ConcurrentModificationException 을 일으킴
~~~java
for(Transaction transcation: transactions) { 
    if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        transactions.remove(transaction)
     }
}
~~~

 - 내부적으로 for-each 루프는 Iterator 객체를 사용하므로 위 코드는 다음과 같이 해석된다.
~~~java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
    Transaction transaction = iterator.next();
    if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        transactions.remove(transaction); // 반복하면서 별도의 두 객체를 통해 컬렉션을 바꾸고 있는 문제
     }
}
~~~
두 개의 개별 객체가 컬렉션을 관리한다는 사실을 주목하자
   - Iterator 객체가 next(), hasNext() 를 이용해 소스를 질의
   - Collection 객체 자체, remove() 를 호출해 요소를 삭제한다.

   - 결과적으로 반복자의 상태는 컬렉션의 상태와 서로 동기화되지 않는다. Iterator 객체를 명시적으로 사용하고 그 객체의 remove() 메서드를 호출함으로 이 문제를 해결할 수 있다.

~~~java
for(Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
    Transaction transaction = iterator.next();
    if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        iterator.remove(); 
     }
}
~~~

 - 이 코드 패턴은 자바 8의 removeIf 메서드로 바꿀 수 있다.
~~~java
transactions.removeif(transaction -> Character.isDigit(transaction.getReferenceCode().charAt(0)))
~~~

### 8.2.2. replaceAll 메서드

 - List 인터페이스의 replaceAll 메서드를 이용해 리스트의 각 요소를 새로운 료소로 바꿀 수 있다.

~~~java
referenceCodes.stream()
        .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
        .collect(Collectors.toList())
        .forEach(System.out::println);
~~~

 - 하지만 이 코드는 새 문자열 컬렉션을 만든다. 기존 컬렉션을 바꾸는것을 원한다면 다음처럼 ListIterator 객체(요소를 바꾸는 set() 메서드를 지원) 을 이용할 수 있다.
~~~java
  for (ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); ) {
      String code = iterator.next();
      iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
  }
  System.out.println(referenceCodes);
~~~

 - 자바 8의 기능을 이용하면 다음처럼 간단하게 구현 가능
~~~java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
System.out.println(referenceCodes);
~~~

## 8.3. 맵 처리
 - java8 에서는 Map 인터페이스에 몇 가지 디폴트 메서드를 추가했다.
### 8.3.1. forEach 메서드
 - BiConsumer(키와 값을 인수로 받음)를 인수로 받는 forEach 메서드를 지원하므로 코드를 조금 더 간단하게 구현할 수 있다.

~~~java
  Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30),
          entry("Olivia", 25),
          entry("Thibaut", 26)
  );

  for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
      String friend = entry.getKey();
      Integer age = entry.getValue();
      System.out.println(friend + " is " + age + " years old");
  }
~~~
 - java8
~~~java
        ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
~~~
### 8.3.2. 정렬 메서드
 - 다음 두 개의 새로운 유틸리티를 이용하면 맵의 항목을 값 또는 키를 기준으로 정렬할 수 있다.
   - Entry.comparingByValue
   - Entry.comparingByKey

~~~java
 Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond"));

        favouriteMovies.entrySet()
                .stream()
                .sorted(Entry.comparingByKey())
                .forEachOrdered(System.out::println);
~~~

### 8.3.3. getOrDefault 메서드 
 - 키가 존재하지 않으면 null 이 반환되므로 NullPointerException 을 방지하려면 요청 결과가 넣인지 확인한 후 기본값을 반환하는 방식으로 이 문제를 해결할 수 있다.
   - getOrDefault 메서드를 이용하면 쉽게 이 문제를 해결할 수 있다.
~~~java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
entry("Olivia", "James Bond"));

System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
System.out.println(favouriteMovies.getOrDefault("Tribaut", "Matrix"));
~~~
### 8.3.4. 계산 패턴
 - 맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황이 필요할 때가 있다.
   - ex:  키를 이용해 값비싼 동작을 실행해서 얻은 결과를 캐시하려 한다.
     - 키가 존재하면 결과를 다시 계산할 필요가 없다.
     - 다음 세가지 연산이 이런 상황에서 도움을 준다.
       - computeIfAbsent : 제공된 키에 해당하는 값이 없거나 null 이라면 키를 이용해 새로운 값을 계산하고 맵에 추가한다.
       - computeIfPresent : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
       - compute : 제공된 키로 새 값을 계산하고 맵에 저장한다. 

여러 값을 저장하는 맵을 처리할 때도 이 패턴을 유용하게 활용할 수 있다.
 - Map<K, List<V>> 에 요소를 추가하려면 항목이 초기화되어 있는지 확인해야 한다.
 - Raphael 에게 줄 영화 목옥을 만든다고 가정
~~~java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
    entry("Olivia", "James Bond"));

Map<String, List<String>> friendsToMovies = new HashMap<>();
System.out.println("--> Adding a friend and movie in a verbose way");
String friend = "Raphael";
List<String> movies = friendsToMovies.get(friend);
if (movies == null) {
    movies = new ArrayList<>();
    friendsToMovies.put(friend, movies);
}
movies.add("Star Wars");
System.out.println(friendsToMovies);
~~~

- computeIfAbsent 는 키가 존재하면 값을 계산해 맵에 추가하고 키가 존재하면 기존값을 반환한다.
  - 아래와 같이 위 코드를 구현 가능하다. 
~~~java
Map<String, List<String>> friendsToMovies = new HashMap<>();
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
        .add("Star Ward")
~~~

 - computeIfPresent 메서드는 현재키와 관련된 값이 맵에 존재하며 널이 아닐 때만 새 값을 계산한다.
   
### 8.3.5. 삭제 패턴
 - 자바 8 에서는 키가 특정한 값과 연관되었을 때만 항목을 제거하는 오버로드 버전 메서드를 제공한다.
~~~java
default boolean remove(Object key, Object value)
~~~
 - 기존에는 다음처럼 코드를 구현했다.
~~~java
String key = "Raphael";
String value = "Jack Reacher 2";

Map<String, String> favouriteMovies = new HashMap<>(
        Map.ofEntries(entry("Raphael", "Jack Reacher 2"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond")));

if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
    favouriteMovies.remove(key);
    return true;
} else {
    return false;
}
~~~

 - 이제 다음처럼 코드를 간결하게 구현할 수 있다.
~~~java
String key = "Raphael";
String value = "Jack Reacher 2";

Map<String, String> favouriteMovies = new HashMap<>(
        Map.ofEntries(entry("Raphael", "Jack Reacher 2"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond")));

favouriteMovies.remove(key, value);
~~~
### 8.3.6. 교체 패턴
 - 맵의 항목을 바꾸는 데 사용할 수 있는 두 개의 메서드가 맵에 추가되었다.
    - replaceAll: BiFunction 을 적용한 결과로 각 항목의 값을 교체한다.
      - List의 replaceAll 과 비슷한 동작 수행
    - replace: 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 존재한다.

~~~java
Map<String, String> favouriteMovies = new HashMap<>(
        Map.ofEntries(entry("Raphael", "Star Wars"),
                entry("Olivia", "james bond")));

favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
System.out.println(favouriteMovies);
~~~
 - replace 패턴은 한 개의 맵에만 적용할 수 있다.

### 8.3.7. 합침 
 - 두 그룹의 연락처를 포함하는 두 개의 맵을 합친다고 가정할 때 다음처럼 putAll 을 사용할 수 있다.
~~~java
   Map<String, String> family = Map.ofEntries(
            entry("Teo", "Star Wars"),
            entry("Cristina", "james bond")
    );
    Map<String, String> friends = Map.ofEntries(
            entry("Raphael", "Star Wars")
    );
    Map<String, String> everyone = new HashMap<>(family);
    everyone.putAll(friends);
    System.out.println(everyone);
~~~
 - 중복된 키가 없다면 위 코드는 잘 동작한다.
   - 중복된 키가 있다면 원하는 동작이 이루어지지 못할 수 있다.
 - 값을 좀 더 유연하게 합쳐야 한다면 새로운 merge 메서드를 이용할 수 있다.
   - 이 메서드는 중복된 키를 어떻게 합칠지 결정하는 BiFunction을 인수로 받는다.
~~~java
Map<String, String> family = Map.ofEntries(
    entry("Teo", "Star Wars"),
    entry("Cristina", "james bond")
);
Map<String, String> friends = Map.ofEntries(
    entry("Raphael", "Star Wars"),
    entry("Cristina", "Matrix")
);
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((k, v) -> everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2)); // 키가 중복될 경우 두 값을 연결
System.out.println(everyone);
~~~
 - merge 를 이용해 초기화 검사를 구현할 수도 있다.
   - 영화를 몇 회 시청했는지 기록하는 맵이 있다고 가정
   - 해당 값을 증가시키기 전에 영화가 이미 맵에 준재하는지 확인해야 한다.
~~~java
Map<String, Long> moviesToCount = new HashMap<>();
String movieName = "JamesBond";
Long count = moviesToCount.get(movieName);
if(count == null) {
    moviesToCount.put(movieName, 1);
} else {
    moviesToCount.put(movieName, count + 1);
}
~~~
아래처럼 구현 가능
~~~java
moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
~~~

## 8.4. 개선된 ConcurrentHashMap
 - 동시성 친화적이며 최신 기술을 반영한 HashMap 버전이다. 
 - 내부 자료구조의 틀정 부분만 잠궈 동시 추가, 갱신 작업을 허용한다.
   - 동기화된 Hashtable 버전에 비해 읽기 쓰기 연산이 월등하다.
### 8.4.1. 리듀스와 검색
 - forEach : 각 (키, 값) 쌍에 주어진 액션을 수행
 - reduce : 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
 - search : 널이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용
 
reduceValues 메서드를 이용해 맵의 최댓값 찾기
~~~java
ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();
long parallelismThreshold = 1;
Optional<Long> maxValue = Optional.ofNullable(concurrentHashMap.reduceValues(parallelismThreshold, Long::max));
System.out.println(maxValue)
~~~
### 8.4.2. 계수
 - 맵의 매핑 개수를 반환하는 mappingCount 메서드를 제공한다. 
 - 기존에 제공되던 size 함수는 int 형으로 반환하지만 long 형으로 반환하는 mappingCount를 사용할 때 매핑의 개수가 int의 범위를 넘어서는 상황에 대하여 대처할 수 있을 것이다.

### 8.4.3. 집합뷰
 - ConcurrentHashMap을 집합 뷰로 반환하는 keySet 메서드를 제공한다.
 - 맵을 바꾸면 집합도 바뀌고 반대로 집합을 바꾸면 맵도 영향을 받는다.
 - newKeySet이라는 메서드를 통해 ConcurrentHashMap으로 유지되는 집합을 만들 수도 있다.

## 8.5. 마치며
 - 자바 9는 적의 원소를 포함하여 바꿀 수 없는 리스트, 집합, 맵을 쉽게 만들 수 있도록 List.of, Set.of, Map.of, Map.ofEntries 등의 컬렉션 팩토리를 지원한다.
 - 이들 컬렉션 팩토리가 반환된 객체는 만들어진 다음 바꿀수 없다.
 - List 인터페이스는 removeIf, replaceAll, sort 세 가지 디폴트 메서드를 지원한다.
 - Set 인터페이스는 removeIf 디폴트 메서드를 지원한다.
 - Map 인터페이스는 자주 사용하는 패턴과 버그를 방지할 수 있도록 다양한 디폴트 메서드를 지원한다.
 - ConcurrentHashMap 은 Map 에서 상속받은 새 디폴트 메서드를 지원함과 동시에 스레드 안전성도 제공한다.