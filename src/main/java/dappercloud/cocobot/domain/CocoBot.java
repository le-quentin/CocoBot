package dappercloud.cocobot.domain;

import java.util.Optional;

public class CocoBot {

    private final Impersonator impersonator;

    public CocoBot(Impersonator impersonator) {
        this.impersonator = impersonator;
    }

    public Optional<MessageReply> handleMessage(Message message) {
        if (message.getText().startsWith("c/")) {
            return Optional.of(handleCommand(message));
        } else {
            handleNonCommandMessage(message);
            return Optional.empty();
        }
    }

    private MessageReply handleCommand(Message message) {
        if ("c/me".equals(message.getText())) {
            return new MessageReply(impersonator.impersonate(message.getAuthor()));
        }
        else {
            return new MessageReply("Je ne connais pas cette commande");
        }
    }

    private void handleNonCommandMessage(Message message) {
        System.out.println("Adding message to model: " + message.getText());
        impersonator.addMessage(message);
    }
}
