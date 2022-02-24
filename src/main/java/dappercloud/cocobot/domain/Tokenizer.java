package dappercloud.cocobot.domain;

import java.util.stream.Stream;

public interface Tokenizer {
    Stream<String> tokenize(String str);
}
