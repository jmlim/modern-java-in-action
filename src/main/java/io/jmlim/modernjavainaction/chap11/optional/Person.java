package io.jmlim.modernjavainaction.chap11.optional;


import java.util.Optional;

public class Person {
    private Optional<Car> car; // 사람이 차를 소유했을 수도 소유하지 않았을 수도 있으므로 Optional 으로 정의
    private int age;
    
    public Optional<Car> getCar() {
        return car;
    }

    public int getAge() {
        return age;
    }}
