package lequentin.cocobot.domain;

import java.util.stream.Stream;

public class SanitizerStringTokenizerDecorator implements StringTokenizer {

    private final StringSanitizer sanitizer;
    private final StringTokenizer tokenizer;

    public SanitizerStringTokenizerDecorator(StringSanitizer sanitizer, StringTokenizer tokenizer) {
        this.sanitizer = sanitizer;
        this.tokenizer = tokenizer;
    }

    @Override
    public Stream<String> tokenize(String str) {
        if (str == null) return Stream.of("");
        return tokenizer.tokenize(sanitizer.sanitize(str));
    }

}
