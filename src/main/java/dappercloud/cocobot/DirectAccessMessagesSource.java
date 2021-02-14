package dappercloud.cocobot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Flux;

public class DirectAccessMessagesSource implements MessagesSource {

    private final GatewayDiscordClient discord;

    public DirectAccessMessagesSource(GatewayDiscordClient discord) {
        this.discord = discord;
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
        return channel.getLastMessageId()
                .map(channel::getMessagesBefore)
                .orElseGet(() -> {
                    System.err.println("Not parsing channel " + channel.getName() + " because cannot get last message id.");
                    return Flux.empty();
                });
    }
}
