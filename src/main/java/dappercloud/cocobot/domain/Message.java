package dappercloud.cocobot.domain;

public class Message {
    private final User author;
    private final String text;

    public Message(User author, String text) {
        this.author = author;
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
