package io.jmlim.modernjavainaction.chap03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class ExecuteAround {
    private static final String FILE = Objects.requireNonNull(ExecuteAround.class.getResource("./data.txt")).getFile();

    public interface BufferedReaderProcessor {
        String process(BufferedReader b) throws IOException;
    }

    public static String processFileLimited() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            return br.readLine();
        }
    }

    public static String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            return p.process(br);
        }
    }

    public static void main(String[] args) throws IOException {
        // 개선 전
        String result = processFileLimited();
        System.out.println(result);

        // 개선 후
//        processFile((BufferedReader b) -> b.readLine());
        String oneLine = processFile(BufferedReader::readLine);
        System.out.println(oneLine);

        String twoLine = processFile(b -> b.readLine() + b.readLine());
        System.out.println(twoLine);

    }
}
