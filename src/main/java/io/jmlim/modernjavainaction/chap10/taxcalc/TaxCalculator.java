package io.jmlim.modernjavainaction.chap10.taxcalc;

import io.jmlim.modernjavainaction.chap10.model.Order;
import io.jmlim.modernjavainaction.chap10.model.Tax;

import java.util.function.DoubleUnaryOperator;

public class TaxCalculator {
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


    private boolean useRegional;
    private boolean useGeneral;
    private boolean useSurcharge;

    public TaxCalculator withTaxRegional() {
        useRegional = true;
        return this;
    }

    public TaxCalculator withTaxGeneral() {
        useGeneral = true;
        return this;
    }

    public TaxCalculator withTaxSurcharge() {
        useSurcharge = true;
        return this;
    }


    public double calculate(Order order) {
        return calculate(order, useRegional, useGeneral, useSurcharge);
    }


    public DoubleUnaryOperator taxFunction = d -> d;

    public TaxCalculator with(DoubleUnaryOperator f) {
        taxFunction = taxFunction.andThen(f);
        return this;
    }

    public double calculateF(Order order) {
        return taxFunction.applyAsDouble(order.getValue());
    }
}
