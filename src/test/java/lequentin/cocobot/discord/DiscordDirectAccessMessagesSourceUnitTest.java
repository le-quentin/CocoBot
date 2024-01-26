package lequentin.cocobot.discord;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import lequentin.cocobot.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscordDirectAccessMessagesSourceUnitTest {

    public static final Snowflake BOT_ID = mock(Snowflake.class);

    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private GatewayDiscordClient discord;

    @Mock
    private DiscordConverter converter;

    @Mock
    private Guild guild;

    @InjectMocks
    private DiscordDirectAccessMessagesSource messagesSource;

    @BeforeEach
    void setUp() {
        when(discord.getGuilds()).thenReturn(Flux.just(guild));
        lenient().when(discord.getSelfId()).thenReturn(BOT_ID);
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @Test
    void shouldGetAllMessagesFromOneChannel() {
        TextChannel channel = mockTextChannel(Type.GUILD_TEXT, Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY);
        when(guild.getChannels()).thenReturn(Flux.just(channel));
        discord4j.core.object.entity.Message discordMessage1 = mockMessage("content");
        discord4j.core.object.entity.Message discordMessage2 = mockMessage("content");
        discord4j.core.object.entity.Message discordMessage3 = mockMessage("   ");
        Snowflake lastMessageId = Snowflake.of(1);
        when(channel.getLastMessageId()).thenReturn(Optional.of(lastMessageId));
        when(channel.getMessagesBefore(lastMessageId)).thenReturn(Flux.just(discordMessage1, discordMessage2, discordMessage3));
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        when(converter.toDomain(discordMessage1)).thenReturn(message1);
        when(converter.toDomain(discordMessage2)).thenReturn(message2);

        Flux<Message> messages = messagesSource.getAllMessages();

        StepVerifier.create(messages)
                .expectNext(message1, message2)
                .expectComplete()
                .verify();
    }

    @ParameterizedTest
    @EnumSource(value = Type.class, mode = Mode.EXCLUDE, names = "GUILD_TEXT")
    void shouldIgnoreNonTextChannels() {
        TextChannel otherChannel = mock(TextChannel.class);
        when(otherChannel.getType()).thenReturn(Type.DM);
        when(guild.getChannels()).thenReturn(Flux.just(otherChannel));

        Flux<Message> messages = messagesSource.getAllMessages();

        StepVerifier.create(messages)
                .expectComplete()
                .verify();
        verifyNoMoreInteractions(otherChannel);
    }

    @Test
    void shouldIgnoreChannelWithoutEnoughPermissions() {
        TextChannel otherChannel = mockTextChannel(Type.GUILD_TEXT, Permission.VIEW_CHANNEL);
        when(guild.getChannels()).thenReturn(Flux.just(otherChannel));

        Flux<Message> messages = messagesSource.getAllMessages();

        StepVerifier.create(messages)
                .expectComplete()
                .verify();
        verify(otherChannel).getType();
        verify(otherChannel).getEffectivePermissions(BOT_ID);
        verifyNoMoreInteractions(otherChannel);
    }

    @Test
    void shouldIgnoreChannelWhenCantFetchLastMessageId() {
        TextChannel channel = mockTextChannel(Type.GUILD_TEXT, Permission.VIEW_CHANNEL, Permission.READ_MESSAGE_HISTORY);
        when(guild.getChannels()).thenReturn(Flux.just(channel));
        when(channel.getLastMessageId()).thenReturn(Optional.empty());
        when(channel.getName()).thenReturn("channelName");

        Flux<Message> messages = messagesSource.getAllMessages();

        StepVerifier.create(messages)
                .expectComplete()
                .verify();
        verify(channel).getLastMessageId();
        verify(channel).getEffectivePermissions(BOT_ID);
        verifyNoMoreInteractions(channel);
        assertThat(errorStreamCaptor.toString()).contains("Not parsing channel channelName because cannot get last message id.");
    }

    private TextChannel mockTextChannel(Type type, Permission... permissions) {
        TextChannel channel = mock(TextChannel.class);
        when(channel.getType()).thenReturn(type);
        lenient().when(channel.getEffectivePermissions(BOT_ID)).thenReturn(Mono.just(PermissionSet.of(permissions)));
        return channel;
    }
    
    private discord4j.core.object.entity.Message mockMessage(String content) {
        discord4j.core.object.entity.Message message = mock(discord4j.core.object.entity.Message.class);
        when(message.getContent()).thenReturn(content);
        return message;
    }
}