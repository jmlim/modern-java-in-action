package io.jmlim.modernjavainaction.chap05;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class PracticalPractice {
    public static void main(String[] args) {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );

        //  1. 2011 년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리하시오.
        List<Transaction> exam1 = transactions.stream().filter(t -> t.getYear() == 2011)
                .sorted(comparing(Transaction::getValue))
                .collect(toList());
        System.out.println(exam1);

        // 2. 거래자가 근무하는 모든 도시를 중복없이 나열하시오.
        List<String> cities = transactions.stream()
                .map(t -> t.getTrader().getCity())
                .distinct()
                .collect(toList());
        System.out.println(cities);

        // 3. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하시오.
        List<Trader> traders = transactions.stream()
                .map(Transaction::getTrader)
                .filter(trader -> trader.getCity().equals("Cambridge"))
                .distinct()
                .sorted(comparing(Trader::getName))
                .collect(toList());
        System.out.println(traders);

        // 4. 모든 거래자의 이름을 알파벳순으로 정렬해서 반환하시오
        /*List<Trader> exam4 = transactions.stream()
               .map(t -> t.getTrader().getName())
                .distinct()
                .sorted()
                .collect(toList());
        System.out.println(exam4);
        */

        // 4. 모든 거래자의 이름을 알파벳순으로 정렬해서 반환하시오 ( 스트링으로 반환)
        String traderStr = transactions.stream()
                .map(t -> t.getTrader().getName())
                .distinct()
                .sorted()
                .reduce("", (n1, n2) -> n1 + n2);
        System.out.println(traderStr);

        //2번째 방법
        String traderStr2 = transactions.stream()
                .map(t -> t.getTrader().getName())
                .distinct()
                .sorted()
                .collect(joining());
        System.out.println(traderStr2);

        // 5. 밀라노에 거래자가 있는가?
        boolean milan = transactions.stream()
                .anyMatch(t -> t.getTrader().getCity().equals("Milan"));
        System.out.println(milan);

        // 6. 케임브리지에 거주하는 거래자의 모든 트랜잭션값을 출력하시오.
        List<Integer> cambridge = transactions.stream()
                .filter(t -> t.getTrader().getCity().equals("Cambridge"))
                .map(Transaction::getValue)
                .collect(toList());
        System.out.println(cambridge);

        // 7. 전체 트랜잭션 중 최댓값은 얼마인가?
        Integer max = transactions.stream()
                .map(Transaction::getValue)
                .reduce(Integer::max)
                .orElse(0);
        System.out.println(max);

        // 8. 전체 트랜잭션 중 최솟값은 얼마인가?
        Integer min = transactions.stream()
                .map(Transaction::getValue)
                .reduce(Integer::min)
                .orElse(0);
        System.out.println(min);
    }
}
