package io.jmlim.modernjavainaction.chap10.mixed;

import io.jmlim.modernjavainaction.chap10.model.Trade;

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
