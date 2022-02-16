package io.jmlim.modernjavainaction.chap10.nestedbuilder;

import io.jmlim.modernjavainaction.chap10.model.Order;
import io.jmlim.modernjavainaction.chap10.model.Stock;
import io.jmlim.modernjavainaction.chap10.model.Trade;

import java.util.stream.Stream;

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
