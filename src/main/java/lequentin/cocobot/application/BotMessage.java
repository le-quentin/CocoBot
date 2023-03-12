package lequentin.cocobot.application;

import org.apache.commons.lang3.StringUtils;

public class BotMessage {
    private final String text;

    public BotMessage(String text) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("Bot message text should not be blank");
        }
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
