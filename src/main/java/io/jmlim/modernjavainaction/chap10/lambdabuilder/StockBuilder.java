package io.jmlim.modernjavainaction.chap10.lambdabuilder;

import io.jmlim.modernjavainaction.chap10.model.Stock;

public class StockBuilder {
    public Stock stock = new Stock();

    public void symbol(String symbol) {
        stock.setSymbol(symbol);
    }
    public void market(String market) {
        stock.setMarket(market);
    }
}
