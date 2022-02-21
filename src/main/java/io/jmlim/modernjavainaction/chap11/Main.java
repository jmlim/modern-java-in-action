package io.jmlim.modernjavainaction.chap11;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        getCarInsuranceName1(new Person());
        getCarInsuranceName2(new Person());

    }

    /**
     * null 안전시도 2: 너무 많은 출구
     *
     * @param person
     * @return
     */
    private static String getCarInsuranceName2(Person person) {
        if (person == null) {
            return "Unknown";
        }

        Car car = person.getCar();
        if (car == null) {
            return "Unknown";
        }

        Insurance insurance = car.getInsurance();
        if (insurance != null) {
            return "Unknown";
        }
        return insurance.getName();
    }

    /**
     * null 안전시도 1: 깊은 의심
     *
     * @param person
     * @return
     */
    private static String getCarInsuranceName1(Person person) {
        if (person != null) {
            Car car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if (insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }


}
