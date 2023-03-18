package lequentin.cocobot.domain;

import java.time.Instant;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return author.equals(message.author) && createdAt.equals(message.createdAt) && text.equals(message.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, createdAt, text);
    }
}
