import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class CocoBot {
    public void handleMessage(Message message) {
        if (message.getContent().startsWith("c/")) {
            handleCommand(message);
        } else {
            handleNonCommandMessage(message);
        }
    }

    private void handleCommand(Message message) {
        final MessageChannel channel = message.getChannel().block();
        if ("c/me".equals(message.getContent())) {
            channel.createMessage("Message rigolo").block();
        }
        else {
            channel.createMessage("Je ne connais pas cette commande").block();
        }
    }

    private void handleNonCommandMessage(Message message) {
        System.out.println("Parsing message: " + message.getContent());
    }
}
