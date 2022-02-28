package lequentin.cocobot.domain;

import java.util.stream.Stream;

public interface StringTokenizer {
    Stream<String> tokenize(String str);
}
