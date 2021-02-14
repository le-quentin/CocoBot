package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import java.util.Optional;

public class CocoBot {

    private final Impersonator impersonator;
    private final MessageClient messageClient;

    public CocoBot(Impersonator impersonator, MessageClient messageClient) {
        this.impersonator = impersonator;
        this.messageClient = messageClient;
    }

    public void handleMessage(Message message) {
        if (message.getContent().startsWith("c/")) {
            handleCommand(message);
        } else {
            handleNonCommandMessage(message);
        }
    }

    private void handleCommand(Message message) {
        if ("c/me".equals(message.getContent())) {
            Optional<User> author = message.getAuthor();
            if (author.isPresent()) {
                String reply = impersonator.impersonate(author.get());
                messageClient.replyToMessage(message, reply);
            } else {
                messageClient.replyToMessage(message, "Y a un problème avec Discord, koâââ koââ");
            }
        }
        else {
            messageClient.replyToMessage(message, "Je ne connais pas cette commande");
        }
    }

    private void handleNonCommandMessage(Message message) {
        System.out.println("Adding message to model: " + message.getContent());
        impersonator.addMessage(message);
    }
}
