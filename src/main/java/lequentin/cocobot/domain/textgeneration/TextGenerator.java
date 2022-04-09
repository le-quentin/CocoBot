package lequentin.cocobot.domain.textgeneration;

public interface TextGenerator {
    void addText(Author author, TextSource textSource);
    String generateText(Author author);
}
