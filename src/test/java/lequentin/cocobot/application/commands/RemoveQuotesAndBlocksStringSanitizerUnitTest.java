package lequentin.cocobot.application.commands;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RemoveQuotesAndBlocksStringSanitizerUnitTest {

    private final RemoveQuotesAndBlocksStringSanitizer sanitizer = new RemoveQuotesAndBlocksStringSanitizer();

    private static Stream<Arguments> sanitizeData() {
        return Stream.of(
                Arguments.of("not a > quote", "not a > quote"),
                Arguments.of(" > not a quote", " > not a quote"),
                Arguments.of(">single line quote", ""),
                Arguments.of(">first quote \n> second quote\n", ""),
                Arguments.of(">first quote \n> second quote\noutside of quotes\n>last quote", "outside of quotes"),
                Arguments.of("```single line block```", ""),
                Arguments.of("```two lines\nblock```", ""),
                Arguments.of("before```two lines\n```after", "beforeafter"),
                Arguments.of("before\n>quote before\n```single line block```after", "before\nafter"),
                Arguments.of("should `preserve` this", "should `preserve` this")
        );
    }

    @MethodSource("sanitizeData")
    @ParameterizedTest
    void shouldSanitize(String text, String expected) {
        assertThat(sanitizer.sanitize(text)).isEqualTo(expected);
    }
}