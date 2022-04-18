package lequentin.cocobot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class SpacePunctuationSanitizerUnitTest {

    private SpacePunctuationSanitizer sanitizer;

    @BeforeEach
    void setUp() {
        sanitizer = new SpacePunctuationSanitizer();
    }


    @ParameterizedTest
    @MethodSource("simplePunctuationArguments")
    void shouldSpaceSimplePunctuationOnlyForRelevantChars(String input, String output) {
        assertThat(sanitizer.sanitize(input)).isEqualTo(output);
    }

    private static Stream<Arguments> simplePunctuationArguments() {
        return Stream.of(
                Arguments.of(",", " , "),
                Arguments.of(";", " ; "),
                Arguments.of(":", " : "),
                Arguments.of("/", " / "),
                Arguments.of("\"", " \" "),
                Arguments.of("(", " ( "),
                Arguments.of(")", " ) "),
                Arguments.of(",,,", " , "),
                Arguments.of(";:,/", " ; "),
                Arguments.of("mot,autremot/,encoreautre", "mot , autremot / encoreautre")
        );
    }

    @ParameterizedTest
    @MethodSource("multiplePunctuationsArguments")
    void shouldKeepOnlyFirstPunctuationInPunctuationsBlocks(String input, String output) {
        assertThat(sanitizer.sanitize(input)).isEqualTo(output);
    }

    private static Stream<Arguments> multiplePunctuationsArguments() {
        return Stream.of(
                Arguments.of(",,,", " , "),
                Arguments.of(",;/", " , "),
                Arguments.of(":/(", " : "),
                Arguments.of("()", " ( "),
                Arguments.of("( )", " (   ) ")
        );
    }

    @ParameterizedTest
    @MethodSource("completeSentencesArguments")
    void shouldSanitizeExampleSentences(String input, String output) {
        assertThat(sanitizer.sanitize(input)).isEqualTo(output);
    }

    private static Stream<Arguments> completeSentencesArguments() {
        return Stream.of(
                Arguments.of("mot,autremot/,encoreautre", "mot , autremot / encoreautre"),
                Arguments.of("Une phrase, écrite normalement (porte-feuille) c'est \"cool\"",
                        "Une phrase ,  écrite normalement  ( porte-feuille )  c'est  \" cool \" "
                )
        );
    }
}