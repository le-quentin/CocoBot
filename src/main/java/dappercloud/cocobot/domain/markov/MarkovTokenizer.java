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

        Queue<String> lastWords = new ArrayDeque<>(words.subList(0, tokenWordsCount));
        WordsTuple firstToken = tokenFromQueue(lastWords);
        Stream<WordsTuple> tokens = words.subList(tokenWordsCount, words.size()).stream()
                .map(word -> {
                    lastWords.remove();
                    lastWords.add(word);
                    return tokenFromQueue(lastWords);
                });
        // Wrapping tuples with EMPTY values, to represent start/end of sentence
        Stream<WordsTuple> allTokens = Stream.concat(Stream.of(firstToken), tokens);
        return Stream.concat(Stream.of(WordsTuple.EMPTY), Stream.concat(allTokens, Stream.of(WordsTuple.EMPTY)));
    }

    private WordsTuple tokenFromQueue(Queue<String> queue) {
        return new WordsTuple(queue);
    }
}
