package dappercloud.cocobot.domain.markov;

import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class WordsTuple {
    private final List<String> words;

    public WordsTuple(String... words) {
        this.words = List.of(words);
    }

    public WordsTuple(Iterable<String> words) {
        this(StreamSupport.stream(words.spliterator(), false).toArray(String[]::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordsTuple that = (WordsTuple) o;
        return words.equals(that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(words);
    }
}
