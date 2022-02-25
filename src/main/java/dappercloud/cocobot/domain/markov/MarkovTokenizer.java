package dappercloud.cocobot.domain.markov;

import dappercloud.cocobot.domain.Tokenizer;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MarkovTokenizer implements Tokenizer {
    public static final String SEPARATOR = ";";

    private final Tokenizer wordsTokenizer;
    private final int tokenWordsCount;

    public MarkovTokenizer(Tokenizer wordsTokenizer) {
        this(wordsTokenizer, 3);
    }

    public MarkovTokenizer(Tokenizer wordsTokenizer, int tokenWordsCount) {
        this.wordsTokenizer = wordsTokenizer;
        this.tokenWordsCount = tokenWordsCount;
    }

    @Override
    public Stream<String> tokenize(String str) {
        List<String> words = wordsTokenizer.tokenize(str)
                .collect(Collectors.toList());
        if (words.size() < tokenWordsCount) return Stream.empty();

        words.add(""); //Adding empty string at the end of the Stream to represent end of the sentence.

        Queue<String> lastWords = new ArrayDeque<>(words.subList(0, tokenWordsCount));
        String firstToken = tokenFromQueue(lastWords);
        Stream<String> tokens = words.subList(tokenWordsCount, words.size()).stream()
                .map(word -> {
                    lastWords.remove();
                    lastWords.add(word);
                    return tokenFromQueue(lastWords);
                });
        return Stream.concat(Stream.of(firstToken), tokens);
    }

    private String tokenFromQueue(Queue<String> queue) {
        return String.join(SEPARATOR, queue);
    }
}
