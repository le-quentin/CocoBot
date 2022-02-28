package lequentin.cocobot.domain;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SentencesStringTokenizer implements StringTokenizer {

    private static final Pattern CONTAINS_FRENCH_WORD_REGEX = Pattern.compile("^.*[a-zàâçéèêëîïôûùüÿñæœ]{2,}.*$", Pattern.CASE_INSENSITIVE);

    @Override
    public Stream<String> tokenize(String str) {
        String message = Optional.ofNullable(str).orElse("");
        return Stream.of(message.split(" ?[.?!]+"))
                .filter(this::containsAtLeastOneFrenchWord)
                .map(String::trim)
                .filter(sentence -> !sentence.isEmpty());
    }

    private boolean containsAtLeastOneFrenchWord(String token) {
        return CONTAINS_FRENCH_WORD_REGEX.matcher(token).matches();
    }
}
