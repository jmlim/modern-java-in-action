# 7. 병렬 데이터 처리와 성능
 - 자바 7은 쉽게 병렬화를 수행하면서 에러를 최소화할 수 있도록 포크/조인 프레임워크(fork/join framework) 기능을 제공한다.
 - 이 장에서는 스트림으로 데이터 컬렉션 관련 동작을 얼마나 쉽게 병렬로 실행할 수 있는지 설명한다.
## 7.1. 병렬 스트림
 - 컬렉션에 parallelStream을 호출하면 병렬 스트림이 생성된다.
   - 병렬 스트림은 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다.
   - 따라서 병렬 스트림을 이용하면 모든 멀티코어 프로세서가 각각의 청크를 처리하도록 할당할 수 있다.

숫자 n 을 인수로 받아서 1부터 n 까지의 모든 숫자의 합계를 반환하는 메서드 구현
~~~java
  public static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
                .limit(n) // n개로 제한
                .reduce(0L, Long::sum);
    }
~~~

전통적인 자바의 구현
~~~java
    public static long iterativeSum(long n) {
        long result = 0;
        for (long i = 0; i <= n; i++) {
            result += i;
        }
        return result;
    }
~~~

n 이 커진다면? 이 연산을 병렬로 처리하는 것이 좋을 것이다. 
 - 무엇부터 건드려야 할까?
   - 동기화, 스레드 갯수, 숫자 생성, 숫자 더하기?
   - 병렬 스트림을 이용 시 이 문제 걱정없이 해결 가능하다.

### 7.1.1. 순차 스트림을 병렬 스트림으로 변환하기
 - 순차 스트림에 parallel 메서드를 호출 시 기존의 함수형 리듀싱 연산(숫자 합계 계산)이 병렬로 처리된다.
~~~java
public static long parallelSum(long n) {
  return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
          .limit(n) // n개로 제한
          .parallel() // 스트림을 병렬 스트림으로 변환
          .reduce(0L, Long::sum);
}
~~~
 - 이전 코드와 다른 점은 스트림이 여러 청크로 분할되어 있다는 것.

 - Stream에서 parallel을 호출하면 내부적으로 이후 연산이 병렬로 수행해야 함을 의미하는 불리언 플래그가 설정된다. 
 - 반대로 sequential로 병렬 스트림을 순차 스트림으로 바꿀 수 있다. 
   - 이 두 메서드를 이용해서 어떤 연산을 병렬로 실행하고 어떤 연산을 순차로 실행할지 제어할 수 있다. 
   - parallel과 sequential 두 메서드 중 최종적으로 호출된 메서드가 전체 파이프라인에 영향을 미친다.
 - 예제) 
~~~java
stream.parallel()
        .filter(,,,)
        .sequential()
        .map(...)
        .parallel()
        .reduce()
~~~

### 7.1.2. 스트림 성능 측정
 - java microbenchmark harness (JMH) 라는 라이브러리를 이용해 작은 벤치마크를 구현할 것이다.
 
 - 자바 11 기준 예제

gradle 설정 추가. 
~~~groovy
plugins {
 .....블라블라..
   // 추가. 
    id 'me.champeau.jmh' version "0.6.2"
}

// 추가. 
wrapper {
    gradleVersion = '6.8.3'
}

... 
// jmh 블록 추가하여 본인이 희망하는 jmh 관련 설정
jmh {
   fork = 1
   warmupIterations = 1
   iterations = 1
}
~~~

gradle/wrapper/gradle-wrapper.properties 에 버전 설정
~~~
gradle-6.8.3-bin.zip
~~~

벤치마킹할 소스 코드 작성
 - src/jmh/java 하위에 작성한다. 

~~~java


@BenchmarkMode(Mode.AverageTime) // 벤치마크 대상 메서드를 실행하는 데 걸린 평균 시간 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 벤치마크 결과를 밀리초 단위로 출력
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"}) // 4Gn의 힙 공간을 제공한 환경에서 벤치마크를 수행해 결과의 신뢰성 확보
@State(Scope.Benchmark)
public class ParallelStreamBenchmark {
   private static final long N = 10_000_000L;

   @Benchmark
   public long sequentialSum() {
      return Stream.iterate(1L, i -> i + 1).limit(N)
              .reduce(0L, Long::sum);
   }

   @Benchmark
   public long iterativeSum() {
      long result = 0;
      for (long i = 1L; i <= N; i++) {
         result += i;
      }
      return result;
   }

   @Benchmark
   public long parallelSum() {
      return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
              .limit(N) // n개로 제한
              .parallel() // 스트림을 병렬 스트림으로 변환
              .reduce(0L, Long::sum);
   }

   @Benchmark
   public long rangedSum() {
      return LongStream.rangeClosed(1, N)
              .reduce(0L, Long::sum);
   }

   @Benchmark
   public long parallelRangedSum() {
      return LongStream.rangeClosed(1, N)
              .parallel()
              .reduce(0L, Long::sum);
   }


   @TearDown(Level.Invocation) // 매 번 벤치마크를 실행한 다음에는 가비지 컬렉터 동작 시도
   public void tearDown() {
      System.gc();
   }
}
~~~

실행 
~~~
./gradlew jmh
~~~

결과는 build/results/jmh 디렉토리에 result.txt 이름으로 생성됨.

결과
 - 순차적 스트림을 사용하는 버전(sequentialSum)에 비해 전통적인 for-loop 방식(iterativeSum)이 4배 더 빠름.
 - 순차적 스트림을 사용하는 버전(sequentialSum)에 비해 병렬스트림(parallelSum) 버전을 실행하면 순차 스트림에 비해 다섯배나 느림.
   - 두가지 문제
     - 반복 결과로 박싱된 객체가 만들어지므로 숫자를 더하려면 언박싱을 해야한다.
     - 반복작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵다.
       - 이전 연산의 결과에 따라 다음 함수의 입력이 달라지기 때문에 iterate 연산을 청그로 분할하기가 어렵다.
     - 병렬 프로그래밍을 오용(병렬과 거리가 먼 반복작업) 을 하면 오히려 전체 프로그램의 성능이 나빠짐
 - rangedClosed 를 사용한 순차 스트림(rangedSum)이 기존 순차형 스트림(sequentialSum)을 사용한 버전에 비해 더 빠르다.
   - LongStream.rangedClosed는 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라짐
   - LongStream.rangedClosed는 쉽게 청크로 분할할 수 있는 숫자 범위를 생산한다. 
     - 예를 들어 1-20 범위의 숫자를 각각 1-5, 6-10, 11-15, 16-20 범위의 숫자로 분할할 수 있다.
 - rangedClosed 를 사용한 병렬 순차 스트림(parallelRangedSum) 은 rangedClosed 를 사용한 순차 스트림(rangedSum) 보다 빠른 성능을 갖는다.

스트림을 병렬화해서 코드 실행 속도를 빠르게 하고 싶으면 항상 병렬화를 올바르게 사용하고 있는지 확인해야 한다.

### 7.1.3. 병렬 스트림의 올바른 사용법
병렬 스트림을 잘못 사용하면서 발생하는 많은 문제는 공유된 상태를 바꾸는 알고리즘을 사용하기 때문에 일어난다. 

다음은 n까지의 자연수를 더하면서 공유된 누적자를 바꾸는 프로그램을 구현한 코드다. 

~~~java

    public static long sideEffectSum(long n) {
        Accumulator accumulator = new Accumulator();
        LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
        return accumulator.total;
    }

    private static class Accumulator {
        public long total = 0;

        public void add(long value) {
            total += value;
        }
    }
~~~

 - 위 코드는 본질적으로 순차 실행할 수 있도록 구현할 수 있으므로 병렬로 실행하면 참사가 일어난다.
 - 특히 total 을 접근할 때마다 (다수의 스레드에서 동시에 데이터에 접근하는) 데이터 레이스 문제가 일어난다.

~~~java
long sideEffectSum = sideEffectSum(10_000_000L);
System.out.println(sideEffectSum);
~~~

 - 메서드의 성능은 둘째치고 올바른 결과값(50000005000000) 이 나오지 않는다.
 - 이 예제처럼 병렬 스트림을 사용했을 때 이상한 결과에 당황하지 않으려면 상태 공유에 따른 부작용을 피해야 한다.

### 7.1.4. 병렬 스트림 효과적으로 사용하기.
 - 확신이 서지 않으면 직접 측정하라. 순차 스트림에서 병렬 스트림으로 쉽게 바꿀 수 있다. 반드시 적절한 벤치마크로 직접 성능을 측정하자
 - 박싱을 주의하라. 되도록이면 기본형 특화 스트림을 사용하자.
 - 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산들을 피하라. limit나 findFirst처럼 요소의 순서에 의존하는 연산을 병렬 스트림에서 수행하려면 비싼 비용을 치러야 한다.
 - 스트림에서 수행하는 전체 파이프라인 비용을 고려하라.
   - 처리해야 할 요소 수가 N, 하나의 요소를 처리하는 데 드는 비용이 Q라 하면 전체 스트림 파이프라인 처리비용은 N * Q로 예상할 수 있다. 
   - Q가 높아진다는 것은 병렬 스트림으로 성능을 개선할 수 있는 가능성이 있음을 의미한다.
 - 소량의 데이터에서는 병렬 스트림이 도움되지 않는다. 병렬화 과정에서 생기는 부가 비용을 상쇄할 수 있을 만큼의 이득을 얻지 못하기 때문이다.
 - 스트림을 구성하는 자료구조가 적절한지 확인하라. 예로, ArrayList는 LinkedList보다 효율적으로 분할할 수 있다. 또한 range 팩토리 메서드로 만든 기본형 스트림도 쉽게 분해할 수 있다. 또한 Spliterator를 구현해서 분해 과정을 완벽하게 제어할 수 있다.
 - 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다.
 - 최종 연산의 병합 비용을 살펴보라.

병렬화 친밀도

|소스|분해성|
|---|---|
|ArrayList|훌륭함|
|LinkedList|나쁨|
|IntStream.range|훌륭함|
|Stream.iterate|나쁨|
|HashSet|좋음|
|TreeSet|좋음|

## 7.2. 포크/조인 프레임워크
 - 포크/조인 프레임워크는 병렬화할 수 있는 작업을 재귀적으로 작은 작업으로 분할한 다음 서브태스크 각각의 결과를 합쳐서 전체 결과를 만들도록 설계되었다.

### 7.2.1. RecursiveTask 활용
 - 쓰레드 풀 이용을 위해선 RecursiveTask의 서브클래스 혹은 RecursiveAction의 서브클래스를 만들어야 한다. RecursiveTask의 R은 병렬화된 태스크가 생성하는 결과 형식을 의미하면 RecursiveAction은 결과 형식이 없을 경우에 사용한다. 
 - RecursiveTask를 이용하기 위해선 compute 메서드를 구현해야 한다. compute 메서드는 태스크를 서브태스크로 분할하는 로직과 더 이상 분할할 수 없을 때 개별 서브태스크의 결과를 생산할 알고리즘을 정의한다.
 - 의사코드
~~~java
if (태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
	순차적으로 태스크 계산
} else {
	태스크를 두 서브 태스크로 분할
	태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
	모든 서브태스크의 연산이 완료될 때까지 기다림
	각 서브태스크의 결과를 합침
}
~~~
#### ForkJoinPool

ForkJoinTask를 실행하기 위한 스레드 풀이다. 태스크를 생성하면 생성한 ForkJoinTask를 ForkJoinPool의 invoke 메서드로 전달한다. 일반적으로 애플리케이션은 둘 이상의 ForkJoinPool을 사용하지 않는다. 소프트웨어의 필요한 곳에서 언제든 가져다 쓸 수 있도록 ForkJoinPool을 한 번만 인스턴스화해서 정적 필드에 싱글턴으로 저장한다. 기본 풀의 크기는 Runtime.availableProcessors

의 반환값으로 결정된다. 이 값은 사용할 수 있는 프로세서의 갯수를 말하지만, 실제로는 프로세서외에 하이퍼스레딩과 관련된 가상 프로세스도 개수에 포함된다

### 7.2.2. 포크/조인 프레임워크를 제대로 사용하는 방법
 - join 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다. join 메서드는 두 서브태스크가 모두 시작된 다음에 호출하자.
 - RecursiveTask 내에서는 ForkJoinPool의 invoke 메서드를 사용하지 말아야 한다. 대신 compute나 fork 메서드를 호출하자
 - 왼쪽 작업과 오른쪽 모두에 fork메서드를 사용하는 것대신, 한쪽 작업에 compute를 호출하자. 두 서브태스크의 한 태스크에는 같은 스레드를 재사용할 수 있으므로 풀에서 불필요한 태스크를 할당하는 오버헤드를 줄일 수 있다.
 - 디버깅이 어렵다는 점을 고려하자.
 - 각 서브태스크의 실행시간은 새로운 태스크를 포킹하는 데 드는 시간보다 길어야 한다.
### 7.2.3. 작업 훔치기
 - 포크/조인 프레임워크에서는 작업 훔치기(work stealing)라는 기법을 사용한다. 각각의 스레드는 자신에게 할당된 태스크를 포함하는 이중 연결 리스트(doubley linked list)를 참조하면서 작업이 끝날 때마다 큐의 헤드에서 다른 태스크를 가져와서 작업을 처리한다. 이때 한 스레드는 다른 스레드보다 자신에게 할당된 태스크를 더 빨리 처리할 수 있는데, 할일이 없어진 스레드는 유휴 상태로 바뀌는 것이 아니라 다른 스레드의 큐의 꼬리에서 작업을 훔쳐온다. 모든 태스크가 작업을 끝낼 때 까지 이 과정을 반복한다. 따라서 태스크의 크기를 작게 나누어야 작업자 스레드 간의 작업부하를 비슷한 수준으로 유지할 수 있다.
## 7.3. Spliterator 인터페이스
  - 자바 8에서 Spliterator가 등장했다. Spliterator는 분할할 수 있는 반복자(spliatable iterator)라는 의미다. 자바 8은 컬렉션 프레임워크에 포함된 모든 자료구조에서 사용할 수 있는 디폴드 Spliterator 구현을 제공한다.

~~~java
public interface Spliterator<T> {
	boolean tryAdvance(Consumer<? super T> action);
	Spliterator<T> trySplit();
	long estimateSize();
	int characteristics();
}
~~~

 - T : Spliterator에서 탐색하는 요소의 형식
 - tryAdvance : Spliterator의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 참을 반환한다.
 - trySplit : Spliterator의 일부 요소를 분할해서 두 번째 Spliterator를 생성하는 메서드
 - estimateSize : 탐색해야 할 요소 수 정보를 제공한다
 - characteristics : Spliter의 특성을 의미한다
### 7.3.1. 분할 과정
### 7.3.2. 커스텀 Spliterator 구현가기

## 7.4. 마치며
 - 내부 반복을 이용하면 명시적으로 다른 스레드를 사용하지 않고도 스트림을 병렬로 처리 할 수 있다.
 - 간단하게 스트림을 병렬로 처리할 수 있지만 항상 병렬 처리가 빠른것은 아니다. 
   - 병렬 처리를 사용했을 때 성능을 직접 측정해봐야 한다.
 - 병렬 스트림으로 데이터 집합을 병렬 실행할 때 특히 처리해야 할 데이터가 아주 많거나 각 요소를 처리하는 데 오랜 시간이 걸릴 때 성능을 높일 수 있다.
 - 가능하면 기본형 특화 스트림을 사용하는 등 올바른 자료구조 선택이 어떤 연산을 병렬로 처리하는 것보다 성능적으로 더 큰 영향을 미칠 수 있다.
 - 포크/조인 프레임워크에서는 병렬화할 수 있는 태스크를 작은 태스크로 분할한 다음에 분할된 태스크를 각각의 스레드로 실행하며 서브태스크 각각의 결과를 합쳐서 최종 결과를 생산한다.
 - Spliterator 는 탐색하려는 데이터를 포함하는 스트림을 어떻게 병렬화할 것인지 정의한다.

추가 참고자료 
 - https://github.com/ckddn9496/modern-java-in-action/blob/main/contents/Chapter%207%20-%20%EB%B3%91%EB%A0%AC%20%EB%8D%B0%EC%9D%B4%ED%84%B0%20%EC%B2%98%EB%A6%AC%EC%99%80%20%EC%84%B1%EB%8A%A5.md
 - https://mong9data.tistory.com/131