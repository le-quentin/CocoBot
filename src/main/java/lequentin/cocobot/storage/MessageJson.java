package lequentin.cocobot.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageJson {
    private String text;

    public MessageJson(@JsonProperty("text") String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
