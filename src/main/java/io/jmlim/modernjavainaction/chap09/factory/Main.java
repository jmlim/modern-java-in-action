package io.jmlim.modernjavainaction.chap09.factory;

public class Main {

    public static void main(String[] args) {
        Product p = ProductFactory.createProduct("loan");
        System.out.println(p);



    }
}
