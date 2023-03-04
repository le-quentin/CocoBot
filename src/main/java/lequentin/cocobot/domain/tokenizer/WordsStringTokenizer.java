package lequentin.cocobot.domain.tokenizer;

import lequentin.cocobot.domain.StringTokenizer;

import java.util.Optional;
import java.util.stream.Stream;

public class WordsStringTokenizer implements StringTokenizer {
    @Override
    public Stream<String> tokenize(String str) {
        String notNullStr = Optional.ofNullable(str).orElse("");
        return Stream.of(notNullStr.split(" +"))
            .filter(word -> !word.isEmpty());
    }
}
