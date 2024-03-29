package lequentin.cocobot.domain.markov;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WordsTuple {
    public static final WordsTuple EMPTY = new WordsTuple(List.of());

    private final List<String> words;

    public WordsTuple(String... words) {
        this.words = List.of(words);
    }

    public WordsTuple(Iterable<String> words) {
        this(StreamSupport.stream(words.spliterator(), false).toArray(String[]::new));
    }

    public String join(String separator) {
        return String.join(separator, words);
    }

    public String lastWord() {
        return words.get(words.size()-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordsTuple that = (WordsTuple) o;
        return toHashableString().equals(that.toHashableString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toHashableString());
    }

    @Override
    public String toString() {
        return "("+String.join(";", words)+")";
    }

    private String toHashableString() {
        return words.stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining(";"));
    }
}
