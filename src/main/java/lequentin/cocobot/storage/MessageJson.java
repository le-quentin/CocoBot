package lequentin.cocobot.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class MessageJson {
    private final Instant createdAt;
    private final String text;

    public MessageJson(
            @JsonProperty("createdAt") Instant createdAt,
            @JsonProperty("text") String text
    ) {
        this.createdAt = createdAt;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
