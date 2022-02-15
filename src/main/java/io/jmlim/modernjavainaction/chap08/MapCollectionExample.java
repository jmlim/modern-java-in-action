package io.jmlim.modernjavainaction.chap08;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.entry;

public class MapCollectionExample {
    public static void main(String[] args) {
        forEachMap();
        forEachMapJava8();

        System.out.println("=======");
        favouriteMoviesSortExample();
        System.out.println("======");
        favouriteMoviesDefaultMethodExample();

        friendToMoviesExample();
        friendToMoviesJava8Example();

        removeRaphaelKey();
        removeRaphaelKeyJava8();

        favouriteMoviesReplaceAll();

        mergeMapUsePutAll();

        mergeMapUseMerge();

        Map<String, Long> moviesToCount = new HashMap<>();
        String movieName = "JamesBond";

        Long cnt = moviesToCount.get(movieName);
        if(cnt == null) {
            moviesToCount.put(movieName, 1L);
        } else {
            moviesToCount.put(movieName, cnt + 1);
        }

        // 아래처럼 구현 가능
        moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
        System.out.println(moviesToCount);


        ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap<>();
        long parallelismThreshold = 1;
        Optional<Long> maxValue = Optional.ofNullable(concurrentHashMap.reduceValues(parallelismThreshold, Long::max));
        System.out.println(maxValue);

    }

    private static void mergeMapUseMerge() {
        Map<String, String> family = Map.ofEntries(
                entry("Teo", "Star Wars"),
                entry("Cristina", "james bond")
        );
        Map<String, String> friends = Map.ofEntries(
                entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix")
        );
        Map<String, String> everyone = new HashMap<>(family);
        // 키가 중복될 경우 두 값을 연결
        friends.forEach((k, v) -> everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2));
        System.out.println(everyone);

    }

    private static void mergeMapUsePutAll() {
        Map<String, String> family = Map.ofEntries(
                entry("Teo", "Star Wars"),
                entry("Cristina", "james bond")
        );
        Map<String, String> friends = Map.ofEntries(
                entry("Raphael", "Star Wars")
        );
        Map<String, String> everyone = new HashMap<>(family);
        everyone.putAll(friends);
        System.out.println(everyone);
    }

    private static void favouriteMoviesReplaceAll() {
        Map<String, String> favouriteMovies = new HashMap<>(
                Map.ofEntries(entry("Raphael", "Star Wars"),
                        entry("Olivia", "james bond")));

        favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
        System.out.println(favouriteMovies);
    }

    private static void friendToMoviesJava8Example() {
        Map<String, List<String>> friendsToMovies = new HashMap<>();
        System.out.println("--> Adding a friend and movie in a verbose way");

        friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>())
                .add("Star Ward");
        System.out.println(friendsToMovies);
    }

    private static void removeRaphaelKeyJava8() {
        String key = "Raphael";
        String value = "Jack Reacher 2";

        Map<String, String> favouriteMovies = new HashMap<>(
                Map.ofEntries(entry("Raphael", "Jack Reacher 2"),
                        entry("Cristina", "Matrix"),
                        entry("Olivia", "James Bond")));

        favouriteMovies.remove(key, value);
    }

    private static boolean removeRaphaelKey() {
        String key = "Raphael";
        String value = "Jack Reacher 2";

        Map<String, String> favouriteMovies = new HashMap<>(
                Map.ofEntries(entry("Raphael", "Jack Reacher 2"),
                        entry("Cristina", "Matrix"),
                        entry("Olivia", "James Bond")));

        if (favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
            favouriteMovies.remove(key);

            System.out.println(favouriteMovies);
            return true;
        } else {
            return false;
        }
    }

    private static void friendToMoviesExample() {
        Map<String, List<String>> friendsToMovies = new HashMap<>();
        System.out.println("--> Adding a friend and movie in a verbose way");
        String friend = "Raphael";
        List<String> movies = friendsToMovies.get(friend);
        if (movies == null) {
            movies = new ArrayList<>();
            friendsToMovies.put(friend, movies);
        }
        movies.add("Star Wars");
        System.out.println(friendsToMovies);
    }

    private static void favouriteMoviesDefaultMethodExample() {
        Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
                entry("Olivia", "James Bond"));

        System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix"));
        System.out.println(favouriteMovies.getOrDefault("Tribaut", "Matrix"));
    }

    private static void favouriteMoviesSortExample() {
        Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
                entry("Cristina", "Matrix"),
                entry("Olivia", "James Bond"));

        favouriteMovies.entrySet()
                .stream()
                .sorted(comparingByKey())
                .forEachOrdered(System.out::println);
    }

    private static void forEachMapJava8() {
        Map<String, Integer> ageOfFriends = getAgeOfFriends();
        ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
    }

    private static void forEachMap() {
        Map<String, Integer> ageOfFriends = getAgeOfFriends();

        for (Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
            String friend = entry.getKey();
            Integer age = entry.getValue();
            System.out.println(friend + " is " + age + " years old");
        }
    }

    private static Map<String, Integer> getAgeOfFriends() {
        return Map.ofEntries(entry("Raphael", 30),
                entry("Olivia", 25),
                entry("Thibaut", 26)
        );
    }
}
