package io.jmlim.modernjavainaction.chap10.methodchainbuilder;

import io.jmlim.modernjavainaction.chap10.model.Order;
import io.jmlim.modernjavainaction.chap10.model.Trade;

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
