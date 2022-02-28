package lequentin.cocobot.domain.markov;

import lequentin.cocobot.domain.StringTokenizer;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MarkovTokenizer {

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

        Queue<String> lastWords = new ArrayDeque<>();
        IntStream.range(0, tokenWordsCount).forEach(i -> lastWords.add(""));
        Stream<WordsTuple> tokens = words.stream()
                .map(word -> {
                    lastWords.remove();
                    lastWords.add(word);
                    return tokenFromQueue(lastWords);
                });
        // Wrapping tuples with EMPTY values, to represent start/end of sentence
        return Stream.concat(Stream.of(WordsTuple.EMPTY), Stream.concat(tokens, Stream.of(WordsTuple.EMPTY)));
    }

    private WordsTuple tokenFromQueue(Queue<String> queue) {
        return new WordsTuple(queue);
    }
}
