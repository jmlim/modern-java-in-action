package io.jmlim.modernjavainaction.chap09.observer;

public class Main {
    public static void main(String[] args) {
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObservers("The queen said her favourite book is Modern Java in Action!");


        System.out.println("=====");
        Feed f2 = new Feed();
        // 람다 표현식 사용
        f2.registerObserver((String tweet) -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY!" + tweet);
            }
        });
        f2.registerObserver((String tweet) -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet more news from London..." + tweet);
            }
        });
        f2.notifyObservers("queen money");

    }
}
