package lequentin.cocobot.discord;

import discord4j.core.spec.MessageCreateSpec;
import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.IncomingMessage;
import lequentin.cocobot.domain.Message;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
    public Mono<Message> reply(BotMessage message) {
        Mono<Message> messageMono = discordMessage.getChannel()
                .subscribeOn(Schedulers.immediate())
                .map(channel -> channel.createMessage(MessageCreateSpec.builder().content(message.getText()).build()))
                .flatMap(messageCreateSpec -> messageCreateSpec.map(converter::toDomain));
        messageMono.subscribe();
        return messageMono;
    }
}
