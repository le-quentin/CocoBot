package lequentin.cocobot.config;

public class Secrets {
    private final String botToken;

    public Secrets(String botToken) {
        this.botToken = botToken;
    }

    public String getBotToken() {
        return botToken;
    }

}
