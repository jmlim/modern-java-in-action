package io.jmlim.modernjavainaction.chap07.wordcount;

import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WordCount {

    public static final String SENTENCE =
            " Nel   mezzo del cammin  di nostra  vita "
                    + "mi  ritrovai in una  selva oscura"
                    + " che la  dritta via era   smarrita ";

    public static void main(String[] args) {
        int countWordsIteratively = countWordsIteratively(SENTENCE);
        System.out.println("countWordsIteratively : " + countWordsIteratively);

        int countWords = countWords(SENTENCE);
        System.out.println("countWords : " + countWords);
    }

    /**
     * 반복형으로 단어 수를 세는 메서드
     *
     * @param s
     * @return
     */
    public static int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) counter++;
                lastSpace = false;
            }
        }
        return counter;
    }


    public static int countWords(String s) {
        /*Stream<Character> stream = IntStream.range(0, s.length())
                .mapToObj(SENTENCE::charAt);*/
        Spliterator<Character> spliterator = new WordCounterSpliterator(s);
        Stream<Character> stream = StreamSupport.stream(spliterator, true);

        return countWords(stream);
    }


    private static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                WordCounter::accumulate,
                WordCounter::combine);

        return wordCounter.getCounter();
    }
}
