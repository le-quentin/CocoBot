package lequentin.cocobot.discord;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessagesSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Set;

public class DiscordDirectAccessMessagesSource implements MessagesSource {

    private static Logger log = LoggerFactory.getLogger(DiscordDirectAccessMessagesSource.class);

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
                .filter(this::canParseChannel)
                .map(channel -> (TextChannel) channel)
                .flatMap(this::allMessagesFlux);
    }

    private boolean canParseChannel(GuildChannel channel) {
        return channel.getType().equals(Type.GUILD_TEXT) &&
                channel.getEffectivePermissions(discord.getSelfId())
                .filter(perm -> perm.containsAll(Set.of(Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY)))
                .hasElement()
                .blockOptional()
                .orElse(false);
    }

    private Flux<Message> allMessagesFlux(TextChannel channel) {
        log.info("Fetching all messages for channel " + channel.getName());
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
