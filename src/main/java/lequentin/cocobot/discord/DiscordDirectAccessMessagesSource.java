package lequentin.cocobot.discord;

import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessagesSource;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Flux;

public class DiscordDirectAccessMessagesSource implements MessagesSource {

    private final GatewayDiscordClient discord;
    private final DiscordConverter converter;

    public DiscordDirectAccessMessagesSource(GatewayDiscordClient discord, DiscordConverter converter) {
        this.discord = discord;
        this.converter = converter;
    }

    @Override
    public Flux<Message> getAllMessages() {
        return discord.getGuilds()
                .flatMap(Guild::getChannels)
                .filter(channel -> channel.getType().equals(Type.GUILD_TEXT))
                .map(channel -> (TextChannel) channel)
                .flatMap(this::allMessagesFlux);
    }

    private Flux<Message> allMessagesFlux(TextChannel channel) {
        System.out.println("Fetching all messages for channel " + channel.getName());
        return channel.getLastMessageId()
                .map(channel::getMessagesBefore)
                .orElseGet(() -> {
                    System.err.println("Not parsing channel " + channel.getName() + " because cannot get last message id.");
                    return Flux.empty();
                })
                .filter(msg -> !msg.getContent().isBlank())
                .map(converter::toDomain);
    }
}
