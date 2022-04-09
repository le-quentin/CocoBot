package lequentin.cocobot.domain.textgeneration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTextGenerator implements TextGenerator {

    private final Map<Author, List<String>> authorLines;

    public SimpleTextGenerator() {
        this.authorLines = new HashMap<>();
    }

    @Override
    public void addText(Author author, TextSource textSource) {
        List<String> lines = getOrCreateAuthorLines(author);
        textSource.getText().filter(line -> !line.isBlank()).forEach(lines::add);
    }

    @Override
    public String generateText(Author author) {
        List<String> lines = authorLines.get(author);
        return String.join(lines.get(2), lines.get(12), lines.get(42));
    }

    private List<String> getOrCreateAuthorLines(Author author) {
        if (!authorLines.containsKey(author)) authorLines.put(author, new ArrayList<>());
        return authorLines.get(author);
    }
}
