package dappercloud.cocobot.application;

import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;

import java.util.Optional;

public class CocoChatBotApplication implements ChatBot {

    private final Impersonator impersonator;

    public CocoChatBotApplication(Impersonator impersonator) {
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
