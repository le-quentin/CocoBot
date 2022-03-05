package lequentin.cocobot.domain;

import java.time.Instant;

public class Message {
    private final User author;
    private final Instant createdAt;
    private final String text;

    public Message(User author, Instant createdAt, String text) {
        this.author = author;
        this.createdAt = createdAt;
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }
}
