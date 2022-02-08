package io.jmlim.modernjavainaction.chap03;

import java.util.function.DoubleUnaryOperator;

public class DoubleIntegrate {
    public static void main(String[] args) {
        double value1 = integrate((double x) -> x + 10, 3, 7);
        System.out.println(value1);

        double value2 = integrate(DoubleIntegrate::f, 3, 7);
        System.out.println(value2);
    }


   /* private static double integrate(DoubleFunction<Double> f, double a, double b) {
        return (f.apply(a) + f.apply(b)) * (b - a) / 2.0;
    }*/

    private static double integrate(DoubleUnaryOperator f, double a, double b) {
        return (f.applyAsDouble(a) + f.applyAsDouble(b)) * (b - a) / 2.0;
    }

    private static Double f(double x) {
        return x + 10;
    }
}
