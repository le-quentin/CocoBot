package dappercloud.cocobot.domain.markov;

import dappercloud.cocobot.domain.StringTokenizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkovTokenizerUnitTest {

    @Mock
    private StringTokenizer wordsStringTokenizer;

    @Test
    void shouldTokenizeIntoBigrams() {
        when(wordsStringTokenizer.tokenize("str")).thenReturn(Stream.of("Just", "a", "random", "sentence"));
        MarkovTokenizer tokenizer = new MarkovTokenizer(wordsStringTokenizer, 2);

        Stream<WordsTuple> tokens = tokenizer.tokenize("str");

        assertThat(tokens).containsExactly(
                WordsTuple.EMPTY,
                new WordsTuple("","Just"),
                new WordsTuple("Just","a"),
                new WordsTuple("a","random"),
                new WordsTuple("random","sentence"),
                WordsTuple.EMPTY
        );
    }

    @Test
    void shouldTokenizeIntoTrigrams() {
        when(wordsStringTokenizer.tokenize("str")).thenReturn(Stream.of("Just", "a", "random", "sentence"));
        MarkovTokenizer tokenizer = new MarkovTokenizer(wordsStringTokenizer, 3);

        Stream<WordsTuple> tokens = tokenizer.tokenize("str");

        assertThat(tokens).containsExactly(
                WordsTuple.EMPTY,
                new WordsTuple("","","Just"),
                new WordsTuple("","Just","a"),
                new WordsTuple("Just","a","random"),
                new WordsTuple("a","random","sentence"),
                WordsTuple.EMPTY
        );
    }
}