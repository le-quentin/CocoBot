package dappercloud.cocobot;

import java.util.stream.Stream;

public interface Tokenizer {
    Stream<String> tokenize(String str);
}
