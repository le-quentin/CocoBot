package lequentin.cocobot.discord;

import discord4j.core.spec.MessageCreateSpec;
import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.IncomingMessage;
import lequentin.cocobot.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.logging.Level;

public class DiscordIncomingMessage implements IncomingMessage {

    private static Logger log = LoggerFactory.getLogger(DiscordIncomingMessage.class);

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
    public Mono<Message> reply(BotMessage message) {
        log.debug("Replying with message: {}", message.getText());
        Mono<Message> messageMono = discordMessage.getChannel()
                .log("lequentin.cocobot.discord.DiscordIncomingMessage-channel", Level.FINE)
                .subscribeOn(Schedulers.immediate())
                .map(channel -> channel.createMessage(MessageCreateSpec.builder().content(message.getText()).build()))
                .log("lequentin.cocobot.discord.DiscordIncomingMessage-message", Level.FINE)
                .flatMap(messageCreateSpec -> messageCreateSpec.map(converter::toDomain))
                .log("lequentin.cocobot.discord.DiscordIncomingMessage-converted", Level.FINE);
        messageMono.subscribe();
        return messageMono;
    }
}
