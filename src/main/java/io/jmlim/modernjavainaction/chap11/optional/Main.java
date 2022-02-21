package io.jmlim.modernjavainaction.chap11.optional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        Optional<Person> optPerson = Optional.of(person);
/*        Optional<String> name = optPerson.map(Person::getCar)
                .map(Car::getInsurance)
                .map(Insurance::getName);*/

/*        String name = optPerson.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");*/
        String name = new Main().getCarInsuranceName(optPerson);
        System.out.println(name);
    }

    public String getCarInsuranceName(Optional<Person> person) {
        return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }

    /**
     * minAge 이상의 나이를 먼은 사람에 대해서만 person의 Insurance Name 값을 리턴하도록 처리
     * @param person
     * @param minAge
     * @return
     */
    public String getCarInsuranceName(Optional<Person> person, int minAge) {
        return person.filter(p -> p.getAge() >= minAge)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }


    public Set<String> getCarInsuranceName(List<Person> persons) {
       return persons.stream()
               .map(Person::getCar) // 사람 목록을 각 사람이 보유한 자동차의 Optional<Car> 스트림으로 변환
               .map(optCar -> optCar.flatMap(Car::getInsurance)) // flatMap 연산을 이용해 Optional<Car>을 해당 Optional<Insurance<로 변환
               .map(optIns -> optIns.map(Insurance::getName))// Optional<Insurance>를 해당 이름의 Optional<String> 으로 변환
               .flatMap(Optional::stream) // Stream<Optional<String>> 을 현재 이름을 포함하는 Stream<String> 으로 변환
               .collect(toSet()); // 결과 문자열을 중복되지 않은 값을 갖도록 집합으로 수집
    }
}
