package lequentin.cocobot.discord;

import discord4j.core.object.entity.channel.MessageChannel;
import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.IncomingMessage;
import lequentin.cocobot.domain.Message;

public class DiscordIncomingMessage implements IncomingMessage {

    private final discord4j.core.object.entity.Message discordMessage;
    private final DiscordConverter converter;

    public DiscordIncomingMessage(discord4j.core.object.entity.Message discordMessage, DiscordConverter converter) {
        this.discordMessage = discordMessage;
        this.converter = converter;
    }

    @Override
    public Message toDomain() {
        return converter.toDomain(discordMessage);
    }

    @Override
    public void reply(BotMessage message) {
        final MessageChannel channel = discordMessage.getChannel().block();
        channel.createMessage(message.getText()).block();
    }
}
