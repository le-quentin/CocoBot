package lequentin.cocobot.domain.textgeneration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileTextSource implements TextSource {

    private final String path;

    public FileTextSource(String path) {
        this.path = path;
    }

    @Override
    public Stream<String> getText() {
        try {
            return Files.lines(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException("Error while reading file", e);
        }
    }
}
