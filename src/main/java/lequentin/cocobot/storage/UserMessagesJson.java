package lequentin.cocobot.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

class UserMessagesJson {
    private final String username;
    private final List<MessageJson> messages;

    public UserMessagesJson(
            @JsonProperty("username") String username,
            @JsonProperty("messages") List<MessageJson> messages
    ) {
        this.username = username;
        this.messages = messages;
    }

    public String getUsername() {
        return username;
    }

    public List<MessageJson> getMessages() {
        return messages;
    }
}
