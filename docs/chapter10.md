# 람다를 이용한 도메인 전용 언어

## 10.1. 도메인 전용 언어
- DSL(domain-specific language) 은 특정 비즈니스 도메인의 문제를 해결하려고 만든 언어.
- 개발팀과 도메인 전문가가 공유하고 이해할 수 있는 코드.

### 10.1.1. DSL 의 장점과 단점
 - DSL의 장점
   - 간결함 : API는 비즈니스 로직을 간편하게 캡슐화하므로 반복을 피할 수 있고 코드를 간결하게 만들 수 있다.
   - 가독성 : 도메인 영역의 용어를 사용하므로 비 도메인 전문가도 코드를 쉽게 이해할 수 있다. 다양한 조직 구성원 간에 코드와 도메인 영역이 공유될 수 있다.
   - 유지보수 : 잘 설계된 DSL로 구현한 코드는 쉽게 유지보수하고 바꿀 수 있다.
   - 높은 수준의 추상화 : DSL은 도메인과 같은 추상화 수준에서 동작하므로 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨긴다.
   - 집중 : 비즈니스 도메인의 규칙을 표현할 목적으로 설계된 언어이므로 프로그래머가 특정 코드에 집중할 수 있다.
   - 관심사분리(SoC) : 지정된 언어로 비즈니스 로직을 표현함으로 애플리케이션의 인프라구조와 관련된 문제와 독립적으로 비즈니스 관련된 코드에서 집중하기가 용이하다.
 - DSL의 단점
   - DSL 설계의 어려움 : 간결하게 제한적인 언어에 도메인 지식을 담는 것이 쉬운 작업은 아니다.
   - 개발 비용 : 코드에 DSL을 추가하는 작업은 초기 프로젝트에 많은 비용과 시간이 소모된다. 또한 DSL 유지보수와 변경은 프로젝트에 부담을 주는 요소다.
   - 추가 우회 계층 : DSL은 추가적인 계층으로 도메인 모델을 감싸며 이 때 계층을 최대한 작게 만들어 성능 문제를 회피한다.
   - 새로 배워야 하는 언어 : DSL을 프로젝트에 추가하면서 팀이 배워야 하는 언어가 한 개 더 늘어난다는 부담이 있다.
   - 호스팅 언어 한계 : 일부 자바 같은 범용 프로그래밍 언어는 장황하고 엄격한 문법을 가졌다. 이런 언어로는 사용자 친화적 DSL을 만들기가 힘들다.

### 10.1.2. JVM 에서 이용할 수 있는 다른 DSL 해결책
 - 외부 DSL과 내부 DSL을 나누는 것.

 - 내부 DSL
   - 역사적으로 자바는 다소 귀찮고, 유연성이 떨어지는 문법 때문에 읽기 쉽고 간단한 DSL을 만드는데 한계가 있었지만 람다 표현식의 등장으로 어느정도 해결될 수 있었다.
   - 익명 내부 클래스를 사용하는 것보다 람다를 사용하면 신호 대비 잡음 비율을 적정 수준으로 유지하는 DSL을 만들 수 있다.
 - 다중 DSL 
   - 같은 자바 바이트코드를 사용하는 JVM 기반 프로그래밍 언어를 이용하여 DSL을 만들 수 있다.
     - 자바가 아니지만 JVM에서 실행됨(스칼라, 그루비 등)
     - JVM에서 실행되는 언어 중에 문법이 간편하고 제약이 적은 언어가 많다.
 - 외부 DSL
   - 프로젝트에 DSL 을 추가하는 세 번째 옵션은 외부 DSL 을 구현하는 것
     - 자신만의 문법과 구문으로 새 언어를 설계해야한다.

## 10.2. 최신 자바 API의 작은 DSL
### 10.2.1. 스트림 API는 컬렉션을 조작하는 DSL
 - Stream 인터페이스는 네이티브 자바 API에 작은 내부 DSL 을 적용한 좋은 예.
### 10.2.2. 데이터를 수집하는 DSL 인 Collectors
 - Collectors는 다중 수준 그룹화를 달성할 수 있도록 합쳐질 수 있다.

## 10.3. 자바로 DSL 을 만드는 패턴과 기법
 - DSL 은 특정 도메인 모델에 적용할 친화적이고 가독성 높은 API를 제공한다.
 - 예제 도메인 모델
   - 주어진 시장에 주식 가격을 모델링하는 순수 자바 빈즈
~~~java
public class Stock {

    private String symbol;
    private String market;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    @Override
    public String toString() {
        return String.format("Stock[symbol=%s, market=%s]", symbol, market);
    }

}

~~~

 - 주어진 가격에서 주어진 양의 주식을 사거나 파는 거래다.
~~~java

public class Trade {

    public enum Type {
        BUY,
        SELL
    }

    private Type type;
    private Stock stock;
    private int quantity;
    private double price;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public double getValue() {
        return quantity * price;
    }

    @Override
    public String toString() {
        return String.format("Trade[type=%s, stock=%s, quantity=%d, price=%.2f]", type, stock, quantity, price);
    }

}

~~~

 - 고객의 요청한 한개 이상의 거래 주문이다. 
~~~java
public class Order {

    private String customer;
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public double getValue() {
        return trades.stream().mapToDouble(Trade::getValue).sum();
    }

    @Override
    public String toString() {
        String strTrades = trades.stream().map(t -> "  " + t).collect(Collectors.joining("\n", "[\n", "\n]"));
        return String.format("Order[customer=%s, trades=%s]", customer, strTrades);
    }
}
~~~
 - BigBank 라는 고객이 요청한 두 거래를 포함하는 주문을 만들어 보자.

~~~java
Order order = new Order();
order.setCustomer("BigBank");

Trade trade1 = new Trade();
trade1.setType(Trade.Type.BUY);

Stock stock1 = new Stock();
stock1.setSymbol("IBM");
stock1.setMarket("NYSE");

trade1.setStock(stock1);
trade1.setPrice(125.00);
trade1.setQuantity(80);
order.addTrade(trade1);

Trade trade2 = new Trade();
trade2.setType(Trade.Type.BUY);

Stock stock2 = new Stock();
stock2.setSymbol("GOOGLE");
stock2.setMarket("NASDAQ");

trade2.setStock(stock2);
trade2.setPrice(375.00);
trade2.setQuantity(50);
order.addTrade(trade2);

System.out.println("Plain:");
System.out.println(order);
~~~
 - 위 코드는 상당히 장황한 편이다. 비 개발자인 도메인 전문가가 위 코드를 이해하고 검증하기를 기대할 수 없기 때문이다. 
 - 조금 더 직접적이고, 직관적으로 도메인 모델을 반영할 수 있는 DSL 이 필요하다.
   - 다양한 방법으로 이를 달성할 수 있다.

### 10.3.1. 메서드 체인
- DSL 에서 가장 흔한 방식 중 하나.
- 이 방법을 이용하면 한 개의 메서드 호출 체인으로 거래주문을 정의할 수 있다.
~~~java
    Order order = forCustomer("BigBank")
                .buy(80)
                .stock("IBM")
                .on("NYSE")
                .at(125.00)
                .sell(50)
                .stock("GOOGLE")
                .on("NASDAQ")
                .at(375.00)
                .end();

        System.out.println(order);
~~~
 - 상당히 코드가 개선되었다. 
 - 결과를 달성하려면 어떻게 DSL 을 구현해야 할까? 
   - 플루언트 API로 도메인 객체를 만드는 몇 개의 빌더를 구현해야 한다.
   - 최상위 수준 빌더를 만들고 주문을 감싼 다음 한개 이상의 거래를 주문에 추가할 수 있어야 한다.
~~~java

public class MethodChainingOrderBuilder {
    public final Order order = new Order(); // 빌더로 감싼 주문

    private MethodChainingOrderBuilder(String customer) {
        order.setCustomer(customer);
    }

    public static MethodChainingOrderBuilder forCustomer(String customer) {
        return new MethodChainingOrderBuilder(customer); // 고객의 주문을 만드는 정적 팩토리 매서드
    }

    public TradeBuilder buy(int quantity) {
        return new TradeBuilder(this, Trade.Type.BUY, quantity); // 주식을 사는 TradeBuilder 만들기;
    }

    public TradeBuilder sell(int quantity) {
        return new TradeBuilder(this, Trade.Type.SELL, quantity); // 주식을 파는 TradeBuilder 만들기;
    }

    public MethodChainingOrderBuilder addTrade(Trade trade) {
        order.addTrade(trade); // 주문에 주식 추가.
        return this; // 유연하게 추가 주문을 만들어 추가할 수 있도록 주문 빌더 자체를 반환
    }
    public Order end() {
        return order; // 주문 만들기를 종료하고 반환
    }
}

~~~

 - 주문 빌더의 buy(), sell() 메서드는 다른 주문을 만들어 추가할 수 있도록 자신을 만들어 반환한다.
~~~java
public class TradeBuilder {
    private final MethodChainingOrderBuilder builder;
    public final Trade trade = new Trade();

    public TradeBuilder(MethodChainingOrderBuilder builder, Trade.Type type, int quantity) {
        this.builder = builder;
        trade.setType(type);
        trade.setQuantity(quantity);
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(builder, trade, symbol);
    }
}
~~~

- 빌더를 계속 이어가려면 Stock 클래스의 인스턴스를 만드는 TradeBuilder 의 공개 메서드를 이용해야 한다.class
~~~java
public class StockBuilder {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();
    public StockBuilder(MethodChainingOrderBuilder builder, Trade trade, String symbol) {
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilderWithStock on(String market) {
        stock.setMarket(market);
        trade.setStock(stock);
        return new TradeBuilderWithStock(builder, trade);
    }
}
~~~
 - StockBuilder 는 주식의 시장을 지정하고, 거래에 주식을 추가하고, 최종 빌더를 반환하는 on() 메서드 한 개를 정의한다.
~~~java
public class TradeBuilderWithStock {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
        this.builder = builder;
        this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price) {
        trade.setPrice(price);
        return builder.addTrade(trade);
    }
}

~~~
 - 한 개의 공개 메서드 TradeBuilderWithStock 은 거래되는 주식의 단위 가격을 설정한 다음 원래 주문 빌더를 반환한다. 
   - 코드에서 볼 수 있듯이 MethodChaningOrderBuilder 가 끝날 때까지 다른 거래를 플루언트 방식으로 추가할 수 있다.
 - 안타깝게도 빌더를 구현해야 한다는 것이 메서드 체인의 단점이다. 
   - 상위 수준의 빌더를 하위 수준의 빌더와 연결할 접착 많은 접착 코드가 필요하다. 
   - 도메인의 객체의 중첩 구조와 일치하게 들여쓰기를 강제하는 방법이 없다는 것도 단점이다.

### 10.3.2. 중첩된 함수 이용
 - 중첩된 함수 DSL 패턴은 이름에서 알 수 있듯이 다른 함수 안에 함수를 이용해 도메인 모델을 만든다. 
~~~java
  Order order = order("BigBank",
          buy(80, stock("IBM", on("NYSE")), at(125.00)),
          sell(50,
                  stock("GOOGLE", on("NASDAQ")), at(375.00))
  );
  System.out.println(order);
~~~
 - 이 방식의 DSL 을 구현하는 코드는 이전 메서드 체인 코드에 비해 간결하다. 
 - 다음 예제 코드의 NestedFunctionOrderBuilder 는 이런 DSL 형식으로 사용자에게 API를 제공할 수 있음을 보여준다. (모든 정적 메서드를 임포트 했다고 가정)
~~~java

public class NestedFunctionOrderBuilder {
    public static Order order(String customer, Trade... trades) {
        Order order = new Order(); // 해당 고객의 주문 만들기
        order.setCustomer(customer);
        Stream.of(trades).forEach(order::addTrade); // 주문에 모든 거래 추가.
        return order;
    }

    public static Trade buy(int quantity, Stock stock, double price) {
        return buildTrade(quantity, stock, price, Trade.Type.BUY); // 주식 매수 거래 만들기
    }

    public static Trade sell(int quantity, Stock stock, double price) {
        return buildTrade(quantity, stock, price, Trade.Type.SELL); // 주식 매도 거래 만들기
    }

    private static Trade buildTrade(int quantity, Stock stock, double price, Trade.Type type) {
        Trade trade = new Trade();
        trade.setQuantity(quantity);
        trade.setType(type);
        trade.setStock(stock);
        trade.setPrice(price);
        return trade;
    }

    public static double at(double price) { // 거래된 주식의 단가를 정의하는 더미 메서드
        return price;
    }

    public static Stock stock(String symbol, String market) {
        Stock stock = new Stock(); // 거래된 주식 만들기
        stock.setSymbol(symbol);
        stock.setMarket(market);
        return stock;
    }
    
    // 주식이 거래된 시장을 정의하는 더미 메서드 정의
    public static String on(String market) {
        return market;
    }
}
~~~
 - 메서드 체인에 비해 함수의 중첩 방식이 도메인 객체 계층 구조에 그대로 반영 된다는 것이 장점이다. 
 - 문제점
   - 결과 DSL 에 더 많은 괄호를 사용해야 함.
   - 인수 목록을 정적 메서드에 넘겨줘야 한다는 제약도 있다.
     - 도메인 객체에 선택사항 필드가 있으면 인수를 생략할 수 있으므로 이 가능성을 처리할 수 있도록 여러 메서드 오버라이드를 구현해야 한다.
   - 마지막으로 인수의 의미가 이름이 아니라 위치에 의해 정의되었다.
### 10.3.3. 람다 표현식을 이용한 함수 시퀀싱
- 다음 DSL 패턴은 람다 표현식으로 정의한 함수 시퀀스를 사용한다.
~~~java
 Order order = LambdaOrderBuilder.order(o -> {
   o.forCustomer("BigBank");
   o.buy(t -> {
       t.quantity(80);
       t.price(125.00);
       t.stock(s -> {
           s.symbol("IBM");
           s.market("NYSE");
       });
   });
   o.sell(t -> {
       t.quantity(50);
       t.price(375.00);
       t.stock(s -> {
           s.symbol("GOOGLE");
           s.market("NASDAQ");
       });
   });
});
System.out.println(order);
~~~
 - 이들 빌더는 메서드 체인 패턴을 이용해 만들려는 객체의 중간 상태를 유지한다.
 - 메서드 체인 패턴에는 주문을 만드는 최상위 수준의 빌더를 가졌지만 이번에는 Consumer 객체를 빌더가 인수로 받음으로 DSL 사용자가 람다 표현식으로 인수를 구현할 수 있게 했다.

~~~java

public class LambdaOrderBuilder {
    private Order order = new Order();

    public static Order order(Consumer<LambdaOrderBuilder> consumer) {
        LambdaOrderBuilder builder = new LambdaOrderBuilder();
        consumer.accept(builder);
        return builder.order;
    }

    public void forCustomer(String customer) {
        order.setCustomer(customer);
    }

    public void buy(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.BUY);
    }

    public void sell(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.SELL);
    }

    public void trade(Consumer<TradeBuilder> consumer, Trade.Type type) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(type);
        consumer.accept(builder);
        order.addTrade(builder.trade);
    }
}
~~~
 - 주문 빌더의 buy() , sell() 메서드는 두 개의 `Consumer<TradeBuilder>` 람다 표현식을 받는다.
 - 이 람다 표현식을 실행하면 다음처럼 주식 매수, 주식 매도 거래가 만들어진다.
~~~java

public class TradeBuilder {
    public Trade trade = new Trade();

    public void quantity(int quantity) {
        trade.setQuantity(quantity);
    }
    public void price(double price) {
        trade.setPrice(price);
    }

    public void stock(Consumer<StockBuilder> consumer) {
        StockBuilder builder = new StockBuilder();
        consumer.accept(builder);
        trade.setStock(builder.stock);
    }
}

~~~
 - 마지막으로 TradeBuilder는 세 번째 빌더의 Consumer 즉 거래된 주식을 받는다.
~~~java
public class StockBuilder {
    public Stock stock = new Stock();

    public void symbol(String symbol) {
        stock.setSymbol(symbol);
    }
    public void market(String market) {
        stock.setMarket(market);
    }
}
~~~
 - 이 패턴은 이전 두 가지 DSL 형식의 두 가지 장점을 더한다.
   - 메서드 체인 패턴처럼 플루언트 방식으로 거래 주문을 정의할 수 있다.
   - 또한 중첩 함수 형식처럼 다양한 람다 표현식의 중첩 수준과 비슷하게 도메인 객체의 계층 구조를 유지한다.
 - 안타깝게도 많은 설정 코드가 필요하며 DSL 자체가 자바 8 람다 표현식 문법에 의한 잡음의 영향을 받는다는 것이 단점이다.

### 10.3.4. 조합하기
 - 한 DSL 에 한 개의 패턴만 사용하라는 법은 없다.

~~~java
   Order order = MixedBuilder.forCustomer("BigBank",
          MixedBuilder.buy(t -> t.quantity(80)
                  .stock("IBM")
                  .on("NYSE")
                  .at(125.00)),
          MixedBuilder.sell(t -> t.quantity(50)
                  .stock("GOOGLE")
                  .on("NASDAQ")
                  .at(375.00)));

  System.out.println(order);
~~~

~~~java

public class MixedBuilder {
    public static Order forCustomer(String customer, TradeBuilder... builders) {
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(builders).forEach(b -> order.addTrade(b.trade));
        return order;
    }

    public static TradeBuilder buy(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.BUY);
    }

    public static TradeBuilder sell(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.SELL);
    }

    private static TradeBuilder buildTrade(Consumer<TradeBuilder> consumer, Trade.Type type) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(type);
        consumer.accept(builder);
        return builder;
    }

}
~~~

~~~java

public class TradeBuilder {

    public Trade trade = new Trade();
    public TradeBuilder quantity(int quantity) {
        trade.setQuantity(quantity);
        return this;
    }

    public TradeBuilder at(double price) {
        trade.setPrice(price);
        return this;
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(this, trade, symbol);
    }
}

~~~

~~~java
public class StockBuilder {
    private final TradeBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    public StockBuilder(TradeBuilder builder, Trade trade, String symbol) {
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilder on(String market) {
        stock.setMarket(market);
        trade.setStock(stock);
        return builder;
    }
}
~~~
 - 세가지 DSL 패턴을 혼용해 가독성 있는 DSL 을 만드는 방법을 보여준다. 
 - 결점은 여러가지 기법을 혼용하고 있으므로 한 가지 기법을 적용한 DSL 에 비해 사용자가 DSL 을 배우는데 오랜 시간이 걸린다는 것이다.

### 10.3.5. DSL 에 메서드 참조 사용하기
 - 주식 거래 도메인 모델에 가른 간단한 기능을 추가. 
   - 다음에서 보여주는 것 처럼 주문의 총 합에 0개 이상의 세금을 추가해 최종값을 계산하는 기능을 추가한다. 

~~~java
public class Tax {

    public static double regional(double value) {
        return value * 1.1;
    }

    public static double general(double value) {
        return value * 1.3;
    }

    public static double surcharge(double value) {
        return value * 1.05;
    }

}
~~~
 - 불리언 플래그 집합을 이용해 주문에 세금 적용
~~~java


public static double calculate(Order order, boolean useRegional, boolean useGeneral, boolean useSurcharge) {
   double value = order.getValue();
   if (useRegional) {
      value = Tax.regional(value);
   }
   if (useGeneral) {
      value = Tax.general(value);
   }
   if (useSurcharge) {
      value = Tax.surcharge(value);
   }
   return value;
}

~~~
실행
~~~java
double value =  calculate(order, true, false, true)
~~~
 - 이 구현의 가독성 문제는 쉽게 드러난다. 불리언 변수의 올바른 순서를 기억하기도 어려우며 어떤 세금이 적용되었는지도 파악하기 어렵다.


 - 다음에서 보여주는 것 처럼 유창하게 불리언 플래그를 설정하는 최소 DSL 을 제공하는 TaxCalculator 를 이용하는 것이 더 좋은 방법이다.
 - 적용할 세금을 유창하게 정의하는 세금 계산기
~~~java

  private boolean useRegional;
  private boolean useGeneral;
  private boolean useSurcharge;

  public TaxCalculator withTaxRegional() {
    useRegional = true;
    return this;
  }

  public TaxCalculator withTaxGeneral() {
    useGeneral= true;
    return this;
  }

  public TaxCalculator withTaxSurcharge() {
    useSurcharge = true;
    return this;
  }

  public double calculate(Order order) {
        return calculate(order, useRegional, useGeneral, useSurcharge);
 }
~~~
실행
 - 다음 코드처럼 TaxCalculator는 지역 세금과 추가 요금은 주문에 추가하고 싶다는 점을 명확하게 보여준다.
~~~java
double value = new TaxCalculator().withTaxRegional()
        .withTaxSurcharge()
        .calculate(order);
~~~

 - 자바의 함수형 기능을 이용하면 더 간결하고 유연한 방식으로 같은 가독성을 달성할 수 있다.

~~~java
public DoubleUnaryOperator taxFunction = d -> d;

 public TaxCalculator with(DoubleUnaryOperator f) {
     taxFunction = taxFunction.andThen(f);
     return this;
 }

 public double calculate(Order order) {
     return taxFunction.applyAsDouble(order.getValue());
 }
~~~

실행
~~~java
double value = new TaxCalculator()
        .with(Tax::regional)
        .with(Tax::surcharge)
        .calculate(order);
~~~

## 10.4. 실생활의 자바 8 DSL
### 10.4.1. jOOQ
 - SQL 은 DSL 이 가장 흔히, 광범위하게 사용하는 분야.
   - JOOQ 는 SQL 을 구현하는 내부작 DSL 로 자바에 직접 내장된형식 안전 언어다. 
   
 - 아래 SQL을 JOOQ DSL 을 이용해 다음처럼 구현 가능하다. 
~~~sql
SELECT * FROM BOOK
WHERE BOOK.PUBLISHED_IN = 2016
ORDER BY BOOK.TITLE
~~~

~~~java
create.selectFrom(BOOK)
        .where(BOOK.PUBLISHED_IN.eq(2016))
        .orderBy(BOOK.TITLE)
~~~
### 10.4.2. 큐컴버
 - 큐컴버는 다른 BDD 프레임워크와 마찬가지로 이들 명령문을 실행할 수 있는 테스트 케이스로 변환횐다. 
### 10.4.3. 스프링 통합
 - 스프링 통합은 유명한 엔터프라이즈 통합 패턴을 지원할 수 있도록 의존성 주입에 기반한 스프링 프로그래밍 모델을 확장한다. 

## 10.5. 마치며
 - DSL 의 주요 기능은 개발자와 도메인 전문가 사이의 간격을 좁히는 것이다. 
 - DSL 은 크게 내부적 DSL 과 외부적 DSL 로 분류할 수 있다.
 - JVM 에서 이용할 수 있는 스칼라, 그루비 등의 다른 언어로 다중 DSL 을 개발할 수 있다. 
   - 하지만 이들을 자바와 통합하려면 빌드 과정이 복잡해지며 자바와의 상호 호환성 문제도 생길 수 있다.
 - 자바의 장황함과 문법적 엄격함 때문에 보통 자바는 내부적 DSL 을 개발하는 언어로는 적합하지 않다.
   - 자바8의 람다 표현식과 메서드 참조 덕분에 상황이 많이 개선되었다.
 - 최신 자바는 자체 API에 작은 DSL 을 제공한다.
   - 이들 Stream , Collectors 클래스 등에서 제공하는 작은 DSL 은 특히 컬렉션 데이터의 정렬, 필터링 , 변환, 그룹화에 유용하다.
 - 자바로 DSL 을 구현할 때 보통 메서드 체인, 중첩함수, 함수 시퀀싱 세 가지 패턴이 사용된다.
   - 각각의 패턴은 장단점이 있지만 모든 기법을 한 개의 DSL 에 합쳐 장점만을 누릴 수 있다.
 - 많은 자바 프레임워크와 라이브러리를 DSL을 통해 이용할 수 있다.
   - jOOQ, 큐컴버 등
