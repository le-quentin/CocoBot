package lequentin.cocobot.domain.textgeneration;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class FileTextSourceIntTest {

    @Test
    void shouldGetText() {
        TextSource source = new FileTextSource(getResourcePath("text-file.txt").toString());

        Stream<String> text = source.getText();

        assertThat(text).containsExactly(
                "line 1",
                "line 2",
                "",
                "line 4"
        );
    }

    @Test
    void shouldGetTextFromBigFile() {
        TextSource source = new FileTextSource(getResourcePath("big-text-file.txt").toString());

        Stream<String> text = source.getText();

        assertThat(text).hasSizeGreaterThan(500);
    }

    private Path getResourcePath(String relativePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return Path.of(classLoader.getResource(relativePath).getPath());
    }
}