package lequentin.cocobot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class WordsStringTokenizerUnitTest {

    private WordsStringTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new WordsStringTokenizer();
    }

    @Test
    void shouldGetEmptyTokensOnNullString() {
        assertThat(tokenizer.tokenize(null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "     ",
    })
    void shouldGetEmptyTokensForInvalidSentences(String invalidSentence) {
        assertThat(tokenizer.tokenize(invalidSentence)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HM",
            "word",
            "anoThErWORd",
            " andAnotherOne, "
    })
    void shouldGetOneTokenForSingleWord(String sentence) {
        assertThat(tokenizer.tokenize(sentence)).containsExactly(sentence.trim());
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void shouldTokenize(String input, Iterable<String> output) {
        assertThat(tokenizer.tokenize(input)).containsExactlyElementsOf(output);
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
            Arguments.of("two words", List.of("two", "words")),
            Arguments.of("two, words", List.of("two,", "words")),
            Arguments.of("we,ir-d words", List.of("we,ir-d", "words")),
            Arguments.of("and a: whole lot of words", List.of("and", "a:", "whole", "lot", "of", "words"))
        );
    }
}