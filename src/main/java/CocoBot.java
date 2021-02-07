import discord4j.core.object.entity.Message;

public class CocoBot {

    private final MessageClient messageClient;

    public CocoBot(MessageClient messageClient) {
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
            messageClient.replyToMessage(message, "Message rigolo");
        }
        else {
            messageClient.replyToMessage(message, "Je ne connais pas cette commande");
        }
    }

    private void handleNonCommandMessage(Message message) {
        System.out.println("Parsing message: " + message.getContent());
    }
}
