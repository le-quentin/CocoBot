package dappercloud.cocobot.domain;

public class MessageReply {
    private final String text;

    public MessageReply(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
