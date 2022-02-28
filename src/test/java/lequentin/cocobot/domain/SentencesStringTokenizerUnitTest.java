package lequentin.cocobot.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class SentencesStringTokenizerUnitTest {

    private SentencesStringTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        tokenizer = new SentencesStringTokenizer();
    }

    @Test
    void shouldGetEmptyTokensOnNullString() {
        assertThat(tokenizer.tokenize(null)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "     ",
            "-_,;:@£$%^&*()][{}",
            "a b c d e f A B C D E F",
    })
    void shouldGetEmptyTokensForInvalidSentences(String invalidSentence) {
        assertThat(tokenizer.tokenize(invalidSentence)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HM OK",
            "A simple capitalized sentence",
            "a simple not capitalized sentence",
            "a longer sentence, with a comma inside"
    })
    void shouldGetOneTokenForSingleSentenceWithNoEndingPunctuation(String sentence) {
        assertThat(tokenizer.tokenize(sentence)).containsExactly(sentence);
    }

    @Test
    void shouldTokenizeSimpleTwoSentencesMessage() {
        assertThat(tokenizer.tokenize("First sentence. Second sentence")).containsExactly("First sentence", "Second sentence");
    }

}