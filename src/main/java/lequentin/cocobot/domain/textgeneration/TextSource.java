package lequentin.cocobot.domain.textgeneration;

import java.util.stream.Stream;

public interface TextSource {
    Stream<String> getText();
}
