package lequentin.cocobot;

import lequentin.cocobot.domain.textgeneration.Author;
import lequentin.cocobot.domain.textgeneration.FileTextSource;
import lequentin.cocobot.domain.textgeneration.SimpleTextGenerator;
import lequentin.cocobot.domain.textgeneration.TextGenerator;
import lequentin.cocobot.domain.textgeneration.TextSource;

import java.nio.file.Path;

public class BigTextMarkovPrototypingApplication {

    private final TextGenerator textGenerator;

    public BigTextMarkovPrototypingApplication(TextGenerator textGenerator) {
        this.textGenerator = textGenerator;
    }

    public static void main(final String[] args) {
        TextGenerator generator = new SimpleTextGenerator();
        TextSource orwell1984 = new FileTextSource(getResourcePath("1984.txt").toString());
        generator.addText(new Author("Orwell"), orwell1984);

        BigTextMarkovPrototypingApplication app = new BigTextMarkovPrototypingApplication(generator);
        app.run();
    }

    public void run() {
        String generated = textGenerator.generateText(new Author("Orwell"));
        System.out.println(generated);
        System.exit(0);
    }

    private static Path getResourcePath(String relativePath) {
        ClassLoader classLoader = BigTextMarkovPrototypingApplication.class.getClassLoader();
        return Path.of(classLoader.getResource(relativePath).getPath());
    }
}
