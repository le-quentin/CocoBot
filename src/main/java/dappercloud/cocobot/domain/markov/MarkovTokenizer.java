package dappercloud.cocobot.domain.markov;

import dappercloud.cocobot.domain.StringTokenizer;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarkovTokenizer {
    public static final String SEPARATOR = ";";

    private final StringTokenizer wordsStringTokenizer;
    private final int tokenWordsCount;

    public MarkovTokenizer(StringTokenizer wordsStringTokenizer, int tokenWordsCount) {
        this.wordsStringTokenizer = wordsStringTokenizer;
        this.tokenWordsCount = tokenWordsCount;
    }

    public Stream<WordsTuple> tokenize(String str) {
        List<String> words = wordsStringTokenizer.tokenize(str)
                .collect(Collectors.toList());
        if (words.size() < tokenWordsCount) return Stream.empty();

        words.add(""); //Adding empty string at the end of the Stream to represent end of the sentence.

        Queue<String> lastWords = new ArrayDeque<>(words.subList(0, tokenWordsCount));
        WordsTuple firstToken = tokenFromQueue(lastWords);
        Stream<WordsTuple> tokens = words.subList(tokenWordsCount, words.size()).stream()
                .map(word -> {
                    lastWords.remove();
                    lastWords.add(word);
                    return tokenFromQueue(lastWords);
                });
        return Stream.concat(Stream.of(firstToken), tokens);
    }

    private WordsTuple tokenFromQueue(Queue<String> queue) {
        return new WordsTuple(queue);
    }
}
