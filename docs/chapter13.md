# 13. 디폴트 메서드
 - 자바 8에서는 기본 구현을 포함하는 인터페이스를 정의하는 두 가지 방법을 제공한다. 첫 번째는 인터페이스 내부에 정적 메서드를 사용하는 것이다. 두 번째는 인터페이스의 기본 구현을 제공할 수 있도록 디폴트 메서드 기능을 사용하는 것이다.
 - 결과적으로 기존 인터페이스를 구현하는 클래스는 자동으로 인터페이스에 추가된 새로운 메서드의 디폴트 메서드를 상속받게 된다. 이렇게 하면 기존의 코드 구현을 바꾸도록 강요하지 않으면서도 인터페이스를 바꿀 수 있다.

## 13.1. 변화하는 API
 - 인기있는 자바 그리기 라이브러리 설계자가 되었다고 가정.
 - API를 릴리즈한지 몇 개월이 지나면서 Resizable 에 몇가지 기능이 부족하다는 사실을 알계 되었다.
   - ex) Resizable 인터페이스에 크기 조절 인수로 모양의 크기를 조절할 수 있는 setRelativeSize 라는 메서드가 있으면 좋을 것 같음.
     - Square, Rectangle 구현도 고침, 문제는 해결?
       - NO , 사용자가 만든 클래스를 우리가 어떻게 할 수 없음.
       - 바로 자바 라이브러리 설계자가 라이브러리를 바꾸고 싶을 때 같은 문제가 발생함.
### 13.1.1. API 버전 1
~~~java

public interface Resizable extends Drawable {
    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);
}
~~~

~~~java
public class Ellipse implements Resizable {
    @Override
    public int getWidth() {
        return 0;
    }
    @Override
    public int getHeight() {
        return 0;
    }
    @Override
    public void setWidth(int width) {
    }
    @Override
    public void setHeight(int height) {
    }
    @Override
    public void setAbsoluteSize(int width, int height) {
    }
    @Override
    public void draw() {
    }
}
~~~

~~~java
public class Game {

    public static void main(String... args) {
        List<Resizable> resizableShapes = Arrays.asList(
                new Square(), new Triangle(), new Ellipse());
        Utils.paint(resizableShapes);
    }

}

~~~

~~~java

public class Utils {
    public static void paint(List<Resizable> l) {
        l.forEach(r -> {
            r.setAbsoluteSize(42, 42);
        });
    }
}

~~~
### 13.1.2. API 버전 2
 - 몇 개월지 지나자 Resizable 을 구현하는 Square와 Rectangle 구현을 개선해달라는 많은 요청을 받았다.
 - 그래서 아래와 같이 고친다면?
~~~java

public interface Resizable extends Drawable {
    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);
    void setRelativeSize(int widthFactor, int heightFactor); // 새로 추가된 메서드라 가정
}
~~~
 - 위와 같이 고치면 몇가지 문제 발생
   1. Resizable 을 구현하는 모든 클래스는 setRelativeSize 메서드를 구현해야 한다.
      1. 만약 라이브러리 사용자가 직접 Ellipse 를 구현했다면 setRelativeSize 메서드는 구현하지 않았을 것이므로 런타임 시 에러 발생할 것.
   2. 사용자는 같은 코드에 예전 버전과 새로운 버전 두 가지 라이브러리를 모두 사용해야 하는 상황이 생김.
 - 디폴트 메서드로 이 문제를 해결할 수 있음.
   - 디폴트 메서드를 이용해서 API를 마꾸면 새롭게 바뀐 인터페이스에서 자동으로 기본 구현을 제고.

## 13.2. 디폴트 메서드란 무엇인가?
 - 자바 8에서 호환성을 유지하면서 API를 바꿀수 있도록 새로운 기능인 디폴트 메서드 제공.
   - 인터페이스는 자신을 구현하는 클래스에서 메서드를 구현하지 않을 수 있는 새로운 메서드 시그니처를 제공한다.

 - 자바 그리기 라이브러리와 게임 예제로 돌아가서 디폴트 메서드를 이용해 setRelativeSize 의 디폴트 구현을 제공한다면 호환성을 유지하면서 라이브러리를 고칠 수 있다.
~~~java
default void setRelativeSize(int widthFactor, int heightFactor) {
   setAbsoluteSize(getWidth() / widthFactor, getHeight() / heightFactor);
}
~~~
 - 자바 8에서는 Collection 인터페이스의 stream 메서드처럼 부지불식간에 많은 디폴트 메서드를 사용한다.
 - List의 sort 메서드도 디폴트 메서드이며 3장에서 소개한 Predicate, Function, Comparator 등 많은 함수형 인터페이스도 Predicate.and 또는 Function.andThen 같은 다양한 디폴트 메서드를 포함한다.
 
추상클래스와 자바 8 인터페이스 차이
1.클래스는 하나의 추상 클래스만 상속받을 수 있지만 인터페이스를 여러개 구현할 수 있다.
2. 추상 클래스는 인스턴스 변수(필드)로 공통 상태를 가질 수 있다. 
   - 하지만 인터페이스는 인스턴스 변수를 가질 수 없다.


## 13.3. 디폴트 메서드 활용 패턴
 - 디폴트 메서드를 이용하는 두가지 발식 설명
   - 선택형 메서드(Optional method)
   - 동작 다중 상송(Multiple inheritance of behavior)
### 13.3.1. 선택형 메서드
- 아마 인터페이스를 구현한 클래스에서 메서드의 내용이 비어있는 상황을 본 적이 있을 것이다.
  - EX) Iterator 인터페이스
    - hasNext, next 뿐 아니라 remove 메서드도 정의하는데 사용자들이 remove 기능은 잘 사용하지 않으므로 자바 8 이전에는 remove 기능을 무시했다.
    - 결과적으로 Iterator 를 구현하는 많은 클래스에서 remove에 빈 구현을 제공했다.
    - 디폴트 메서드를 이용하면 remove 같은 메서드에 기본 구현을 제공할 수 있으므로 인터페이스를 구현하는 클래스에서 빈 구현을 제공할 필요가 없어짐.
~~~java
// 자바 8의 Iterator 인터페이스는 다음처럼 remove 메서드를 정의한다.
interface Iterator<T> {
    boolean hasNext();
    T next();
    default void remove() {
        throw new UnsupportedOperationException();
    }
}
~~~
- Iterator 인터페이스를 구현하는 클래스는 빈 remove 메서드를 구현할 필요가 없어졌고, 불필요한 코드를 줄일 수 있게 됨.

### 13.3.2. 동작 다중 상속
 - 자바에서 클래스는 한 대의 다른 클래스만 상속할 수 있지만 인터페이스는 여러개 구현할 수 있다.
#### 다중 상속 형식
 - ArrayList 는 AbstractList, List, RandomAccess, Cloneable, Serializable, Iterable, Collection의 서브형식이 된다.
 - 따라서 디폴트 메서드를 사용하지 않아도 다중 상속을 활용할 수 있다.
 - 자바 8에서는 인터페이스가 구현을 포함할 수 있으므로 클래스는 여러 인터페이스에서 동작(구현 코드)을 상속받을 수 있다.
   - 중복되지 않는 최소한의 인터페이스를 유지한다면 코드에서 동작을 쉽게 재사용하고 조합할 수 있다.

#### 기능이 중복되지 않는 최소의 인터페이스
 - 어떤 모양은 회전할 수 없지만 크기는 조절할 수 있고 어떤 모양은 회전할 수 있으며 움직일 수 있지만 크기는 조절할 수 없음.
   - 최대한 기존 코드를 재사용해서 이 기능을 구현하려면?
 - 먼저 setRotationAngle, getRotationAngle 두개의 추상 메서드를 포함하는 Rotatable 정의
 - 그리고 setRotationAngle, getRotationAngle 을 이용해서 디폴트 메서드 rotateBy 구현.
~~~java
public interface Rotatable {
    void setRotationAngle(int angleInDegreses);

    int getRotationAngle();

    default void rotateBy(int angleInDegrees) {
        setRotationAngle((getRotationAngle() + angleInDegrees) % 360);
    }
}

~~~
- Rotatable을 구현하는 모든 클래스는 setRotationAngle, getRotationAngle 의 구현을 제공해야 하지만 rotateBy 는 기본 구현이 제공되므로 따로 구현을 제공하지 않아도 됨
- 마찬가지로 이전에 살펴본 두 가지 인터페이스 Movable과 Resizable 을 정의해야 한다.
  - 두 인터페이스 모두 디폴트 구현을 제공
~~~java

public interface Moveable {
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);

    default void moveHorizontally(int distance) {
        setX(getX() + distance);
    }

    default void moveVertically(int distance) {
        setY(getY() + distance);
    }
}

~~~

~~~java
public interface Resizable {

    int getWidth();

    int getHeight();

    void setWidth(int width);

    void setHeight(int height);

    void setAbsoluteSize(int width, int height);

    /**
     * 디폴트 메서드를 이용해서 setRelativeSize 의 디폴트 구현을 제공한다면 호환성을 유지하면서 라이브러리를 고칠 수 있다.
     */
    default void setRelativeSize(int widthFactor, int heightFactor) {
       setAbsoluteSize(getWidth() / widthFactor, getHeight() / heightFactor);
    }
}

~~~

#### 인터페이스 조합
 - 이제 이들 인터페이스를 조합해서 게임에 필요한 다양한 클래스를 구현할 수 있다.
 - 예를 들어 다음 코드처럼 움직일 수 있고(moveable), 회전할 수 있으며(rotatable), 크기를 조절할 수 있는(resizable) 괴물 (Monster) 클래스를 구현할 수 있다.
~~~java
public class Monster implements Rotatable, Moveable, Resizable {
    ...
}
~~~
 - 몬스터 클래스는 Rotatable, Moveable, Resizable 인터페이스의 디폴트 메서드를 자동으로 상속받음.
   - rotateBy, moveHorizontally, moveVertically, setRelativeSize 구현을 상속받는다.
 - 이번에는 움직일 수 있으며 회전할 수 있지만 크기는 조절할 수 없는 Sun 클래스를 정의한다.
   - 이때 코드를 복사&붙여넣기 할 필요가 전혀 없다. Movable과 Rotatable을 구현할 때 자동으로 디폴트 메서드를 재사용할 수 있기 때문이다.
~~~java
public class Sun implements Moveable, Rotatable {
    ... 모든 추상 메서드의 구현은 제공해야 하지만 디폴트 메서드의 구현은 제공할 필요 X
}
~~~

 - 인터페이스에 디폴트 구현을 포함시키면 또 다른 장점이 생긴다.
   - ex) moveVerically 의 구현을 더 효율적으로 고쳐야 할 때 디폴트 메서드 덕분에 Moveable 인터페이스를 직접 고칠 수 있고 따라서 Moveable 을 구현하는 모든 클래스도 자동으로 변경한 코드를 상속받는다.

## 13.4. 해석 규칙
 - 클래스는 여러 인터페이스를 구현할 수 있다. 그렇기에 같은 시그니처를 갖는 디폴트 메서드를 상속받는 상황이 생길 수 있다.

### 13.4.1. 알아야 할 세 가지 해결 규칙
- 클래스가 항상 이긴다. 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권을 갖는다.
- 이외의 상황에서는 서브인터페이스가 이긴다. 상속관계를 갖는 인터페이스에서 같은 시그니처를 갖는 메서드를 정의할 때는 서브 인터페이스가 이긴다.
  - 즉, B 가 A를 상속받는다면 B가 A를 이긴다.
- 여전히 디폴트 메서드의 우선순위가 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.

### 13.4.2. 디폴트 메서드를 제공하는 서브 인터페이스가 이긴다.
 - B와 A를 구현하는 클래스 C가 등장하는 예제
~~~java
public interface  A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

public interface B extends A { 
    default void hello() {
        System.out.println("Hello from B");
    }
}

public class C implements B, A {
    public static void main(String[] args) {
        new C().hello();
    }
}
~~~

- 서브 인터페이스가 이긴다.
  - 즉 B가 A를 상속받았으므로 컴파일러는 B의 hello 를 선택함.
    - Hello from B 출력.

- 아래 예제에서 이번에는 C가 D를 상속받는다면? 어떤일이 일어날지 생각해보자.
~~~java
public class D implements A {}
public class C extends D implements B, A {
    public static void main(String[] args) {
        new C().hello();
    }
}
~~~
 - 클래스의 메서드 구현이 이긴다고 설명했다.
 - D는 hello를 오버라이드 하지 않았고 단순히 인터페이스 A를 구현
 - 따라서 D는 인터페이스 A의 디폴트 메서드 구현을 상속받는다.
 - 클래스나 규퍼클래스에 메서드 정의가 없을 때는 디폴트 메서드를 정의하는 서브 인터페이스가 선택된다.
 - 따라서 컴파일러는 인터페이스 A의 hello 나 인터페이스 B의 hello 둘 중 하나를 선택해야 한다.
   - 여기서 B가 A를 상속받는 관계이므로 이번에도 "Hello from B" 가 출력된다.

 - 만약 아래의 경우처럼 D가 구현되어 있다면?
~~~java
public class D implements A {
    void hello() {
        System.out.println("Hello from D");
    }
}

public class C extends D implements B, A {
    public static void main(String[] args) {
        new C().hello();
    }
}
~~~

 - "Hello from D" 출력. 규칙 1에 의해 슈퍼클래스의 메서드 정의가 우선권을 갖기 때문이다.
 - 만약 abstract 메서드로 hello 를 구현했다면 A에서 디폴트 메서드를 제공함에도 불구하고 C는 hello 를 구현해야 한다.
~~~java
public abstract class D implements A {
    public abstract void hello();
}
~~~
### 13.4.3. 충돌 그리고 명시적인 문제 해결
 - B가 A를 상속받지 않는 상황이라고 가정
~~~java
public interface  A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

public interface B  {
    default void hello() {
        System.out.println("Hello from B");
    }
}

public class C implements B, A {
    public static void main(String[] args) {
        new C().hello();
    }
}
~~~

 - 이번에는 인터페이스 간에 상속관계가 없으므로 2번 규칙을 적용할 수 없다. 
 - 그러므로 A와 B의 hello 메서드를 구별할 기준이 없다.
   - 따라서 자바 컴파일러는 어떤 메서드를 호출해야 할지 알 수 없으므로 "Error: class C inherits unrelated defaults for hello() from types B and A." 같은 에서가 발생한다.

#### 충돌 해결
 - 개발자가 직접 클래스 C에서 사용하려는 메서드를 명시적으로 선택해야 한다.
 - 지비 8에서는 X.super.m(..) 형태의 새로운 문법을 제공한다. 여기서 X는 호출하려는 메서드 m 의 슈퍼 인터페이스이다.
 - 예를 들어 다음처럼 C에서 B의 인터페이스를 호출 할 수 있다.
~~~java
public class C implements B, A {
    void hello() {
        B.super.hello(); // 명시적으로 인터페이스 B의 메서드 선택
    }
}
~~~

### 13.4.4. 다이아몬드 문제
 - C++ 커뮤니티를 긴장시킬 만한 마지막 시나리오
~~~java
public interface  A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

public interface B extends A {
    
}

public interface C extends A {
    
}

public class D implements B, C {
    public static void main(String[] args) {
        new D().hello();
    }
}
~~~
 - 다이어그램의 모양이 다이아몬드를 닮았으므로 이를 다이아몬드 문제라고 부른다.
 - D는 B와 C 중 누구의 디폴트 메서드 정의를 상속받을까?
   - 실제로 선택할 수 있는 메서드 선언은 하나뿐. A만 디폴트 메서드 정의
   - 결국 프로그램 출력은 "Hello from A" 가 된다.
 - 다음처럼 C에 추상 메서드 hello(); (디폴트 메서드가 아님)을 추가하면 어떤일이 벌어질까?
   - C는 A를 상속받으므로 C의 추상 메서드 hello가 A의 디폴트 메서드 hello 보다 우선권을 갖는다.
   - 따라서 컴파일 에러가 발생하며 클래스 D가 어떤 hello 를 사용할지 명시적으로 선택해서 에러를 해결해야 한다.
   

## 13.5. 마치며
- 자바 8 인터페이스는 구현 코드를 포함하는 디폴트 메서드, 정적 메서드를 정의할 수 있다.
- 디폴트 메서드의 정의는 default 키워드로 시작하며 일반 클래스 메서드처럼 바디를 갖는다.
- 공개된 인터페이스에 추상 메서드를 추가하면 소스 호환성이 깨진다.
- 디폴트 메서드 덕분에 라이브러리 설계자가 API를 바꿔도 기존 버전과 호환성을 유지할 수 있다.
- 선택형 메서드와 동작 다중 상속에도 디폴트 메서드를 사용할 수 있다.
- 클래스가 같은 시그니처를 갖는 여러 디폴트 메서드를 상속하면서 생기는 충돌 문제를 해결하는 규칙이 있다.
- 클래스나 슈퍼클래스에 정의된 메서드가 다른 디폴트 메서드 정의보다 우선한다. 이 외의 상황에서는 서브 인터페이스에서 제공하는 디폴트 메서드가 선택된다.
- 두 메서드의 시그니처가 같고, 상속관계로도 충돌 문제를 해결할 수 없을 때는 디폴트 메서드를 사용하는 클래스에서 메서드를 오버라이드해서 어떤 디폴트 메서드를 호출할지 명시적으로 결정해야 한다.

