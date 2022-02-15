package io.jmlim.modernjavainaction.chap08;

import io.jmlim.modernjavainaction.chap05.Trader;

import java.util.Objects;

public class Transaction {

    private Trader trader;
    private int year;
    private int value;
    private String referenceCode;

    public Transaction(Trader trader, int year, int value, String referenceCode) {
        this.trader = trader;
        this.year = year;
        this.value = value;
        this.referenceCode = referenceCode;
    }

    public Trader getTrader() {
        return trader;
    }

    public int getYear() {
        return year;
    }

    public int getValue() {
        return value;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + (trader == null ? 0 : trader.hashCode());
        hash = hash * 31 + year;
        hash = hash * 31 + value;
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Transaction)) {
            return false;
        }
        Transaction o = (Transaction) other;
        boolean eq = Objects.equals(trader, o.getTrader());
        eq = eq && year == o.getYear();
        eq = eq && value == o.getValue();
        return eq;
    }

    @SuppressWarnings("boxing")
    @Override
    public String toString() {
        return String.format("{%s, year: %d, value: %d}", trader, year, value);
    }

}
