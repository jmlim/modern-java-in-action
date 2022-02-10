package io.jmlim.modernjavainaction.chap05;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class UseFlatMap {
    public static void main(String[] args) throws IOException {
        List<String> words = Arrays.asList("Hello", "World");

        List<String> collect = words.stream()
                .map(w -> w.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(toList());
        System.out.println(collect);

        // ---

        String[][] sample = new String[][]{
                {"a", "b"}, {"c", "d"}, {"e", "a"}, {"a", "h"}, {"i", "j"}
        };

        List<List<String>> collect1 = Arrays.stream(sample)
                .map(Arrays::stream)
                .map(o -> o.collect(toList()))
                .collect(toList());
        System.out.println(collect1);

        List<String> collect2 = Arrays.stream(sample)
                .flatMap(Arrays::stream)
                .collect(toList());
        System.out.println(collect2);

        List<String> collect3 = Arrays.stream(sample)
                .map(Arrays::stream)
                .map(o -> o.collect(toList()))
                .flatMap(Collection::stream) // 생성된 스트림을 하나의 스트림으로 평면화
                .collect(toList());
        System.out.println(collect3);



/*        long uniqueWords = Files.lines(Paths.get("C:\\Users\\1234\\IdeaProjects\\modern-java-in-action\\src\\main\\resources\\io\\jmlim\\modernjavainaction\\chap05\\data.txt"),
                        Charset.defaultCharset())
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .distinct()
                .count();

        System.out.println("There are " + uniqueWords + " unique words in data.txt");*/

      /*  List<String> collect4 = Files.lines(Paths.get("C:\\Users\\1234\\IdeaProjects\\modern-java-in-action\\src\\main\\resources\\io\\jmlim\\modernjavainaction\\chap05\\data.txt"),
                        Charset.defaultCharset())
                .flatMap(line -> Arrays.stream(line.split(" ")))
                .collect(toList());
        System.out.println(collect4);*/

        List<String> collect4 = Files.lines(Paths.get("C:\\Users\\1234\\IdeaProjects\\modern-java-in-action\\src\\main\\resources\\io\\jmlim\\modernjavainaction\\chap05\\data.txt"),
                        Charset.defaultCharset())
                .flatMap(String::lines)
                .collect(toList());
        System.out.println(collect4);

        List<String> collect5 = Files.lines(Paths.get("C:\\Users\\1234\\IdeaProjects\\modern-java-in-action\\src\\main\\resources\\io\\jmlim\\modernjavainaction\\chap05\\data.txt"),
                        Charset.defaultCharset())
                .map(line -> line.split(" "))
                .flatMap(Arrays::stream)
                .collect(toList());
        System.out.println(collect5);


    }
}
