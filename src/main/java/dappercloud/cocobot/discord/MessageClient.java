package dappercloud.cocobot.discord;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class MessageClient {
    public void replyToMessage(Message message, String reply) {
        if (reply.trim().isBlank()) {
            throw new IllegalArgumentException("Reply should not be blank");
        }
        final MessageChannel channel = message.getChannel().block();
        channel.createMessage(reply).block();
    }
}
