package lequentin.cocobot.domain.tokenizer;

import lequentin.cocobot.domain.StringSanitizer;
import lequentin.cocobot.domain.StringTokenizer;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SentencesStringTokenizer implements StringTokenizer {

    private static final Pattern CONTAINS_FRENCH_WORD_REGEX = Pattern.compile("^.*[a-zàâçéèêëîïôûùüÿñæœ]{2,}.*$", Pattern.CASE_INSENSITIVE);

    private final Optional<StringSanitizer> sanitizer;

    public SentencesStringTokenizer() {
        this(null);
    }

    public SentencesStringTokenizer(StringSanitizer sanitizer) {
        this.sanitizer = Optional.ofNullable(sanitizer);
    }

    @Override
    public Stream<String> tokenize(String str) {
        final String message = Optional.ofNullable(str).orElse("");
        final String sanitized = sanitizer.map(sanitizer -> sanitizer.sanitize(message)).orElse(message);
        return Stream.of(sanitized.split(" ?[.?!]+"))
                .filter(this::containsAtLeastOneFrenchWord)
                .map(String::trim)
                .filter(sentence -> !sentence.isEmpty());
    }

    private boolean containsAtLeastOneFrenchWord(String token) {
        return CONTAINS_FRENCH_WORD_REGEX.matcher(token).matches();
    }
}
