package io.jmlim.modernjavainaction.chap08;

import io.jmlim.modernjavainaction.chap05.Trader;

import java.util.*;

import static java.util.Map.entry;
import static java.util.stream.Collectors.toList;

public class CollectionExample {
    public static void main(String[] args) {
        listFactory();
        setFactory();

        //  duplicateSetFactory();

        mapFactory1();
        mapFactory2();


//        forEachRemove();
        iteratorRemove();
        useRemoveIf();

        List<String> convertAllReferenceCodesToUppercase = getConvertAllReferenceCodesToUppercase();
        System.out.println(convertAllReferenceCodesToUppercase);

        convertAllReferenceCodesToUppercase();
        convertAllReferenceCodesToUppercaseJava8();

    }

    private static void convertAllReferenceCodesToUppercaseJava8() {
        List<String> referenceCodes = getReferenceCodes();
        referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
        System.out.println(referenceCodes);
    }

    private static List<String> getReferenceCodes() {
        List<Transaction> transactions = getTransactions();
        return transactions.stream().map(Transaction::getReferenceCode)
                .collect(toList());
    }

    private static void convertAllReferenceCodesToUppercase() {
        System.out.println("====================");
        List<String> referenceCodes = getReferenceCodes();

        System.out.println(referenceCodes);
        for (ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext(); ) {
            String code = iterator.next();
            iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
        }
        System.out.println(referenceCodes);
        System.out.println("====================");
    }

    private static List<String> getConvertAllReferenceCodesToUppercase() {
        List<String> referenceCodes = getReferenceCodes();

        return referenceCodes.stream()
                .map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
                .collect(toList());

    }

    private static void useRemoveIf() {
        List<Transaction> transactions = getTransactions();

        transactions.removeIf(t -> Character.isDigit(t.getReferenceCode().charAt(0)));
        System.out.println(transactions);
    }

    private static void iteratorRemove() {
        List<Transaction> transactions = getTransactions();
        for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
            Transaction transaction = iterator.next();
            if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
                iterator.remove(); // 반복하면서 별도의 두 객체를 통해 컬렉션을 바꾸고 있는 문제
            }
        }

        System.out.println(transactions);
    }

    private static void forEachRemove() {
        List<Transaction> transactions = getTransactions();

        for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext(); ) {
            Transaction transaction = iterator.next();
            if (Character.isDigit(transaction.getReferenceCode().charAt(0))) {
                transactions.remove(transaction); // 반복하면서 별도의 두 객체를 통해 컬렉션을 바꾸고 있는 문제
            }
        }

        System.out.println(transactions);
    }

    private static List<Transaction> getTransactions() {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario", "Milan");
        Trader alan = new Trader("Alan", "Cambridge");
        Trader brian = new Trader("Brian", "Cambridge");

        return new ArrayList<>(List.of(
                new Transaction(brian, 2011, 300, "101"),
                new Transaction(raoul, 2012, 1000, "112"),
                new Transaction(raoul, 2011, 400, "z11"),
                new Transaction(mario, 2012, 710, "b13"),
                new Transaction(mario, 2012, 700, "a12"),
                new Transaction(alan, 2012, 950, "C01")
        ));
    }

    private static void mapFactory2() {
        Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30),
                entry("Olivia", 25),
                entry("Thibaut", 26)
        );
    }

    private static void mapFactory1() {
        Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
        System.out.println(ageOfFriends);
    }

    private static void duplicateSetFactory() {
        Set<String> friends = Set.of("Raphael",
                "Olivia",
                "Olivia");
        System.out.println(friends);
    }

    private static void setFactory() {
        Set<String> friends = Set.of("Raphael",
                "Olivia",
                "Thibaut");
        System.out.println(friends);
    }

    private static void listFactory() {
        List<String> friends = List.of("Raphael",
                "Olivia",
                "Thibaut");
        System.out.println(friends);
    }
}
