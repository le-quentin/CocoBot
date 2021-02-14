package dappercloud.cocobot;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.core.object.entity.channel.TextChannel;
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
import reactor.test.StepVerifier;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectAccessMessagesRepositoryUnitTest {

    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private GatewayDiscordClient discord;

    @Mock
    private Guild guild;

    @InjectMocks
    private DirectAccessMessagesRepository repository;

    @BeforeEach
    void setUp() {
        when(discord.getGuilds()).thenReturn(Flux.just(guild));
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @Test
    void shouldGetAllMessagesFromOneChannel() {
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        TextChannel channel = mock(TextChannel.class);
        Snowflake lastMessageId = mock(Snowflake.class);
        when(channel.getLastMessageId()).thenReturn(Optional.of(lastMessageId));
        when(channel.getMessagesBefore(lastMessageId)).thenReturn(Flux.just(message1, message2));
        when(channel.getType()).thenReturn(Type.GUILD_TEXT);
        when(guild.getChannels()).thenReturn(Flux.just(channel));

        Flux<Message> messages = repository.getAllMessages();

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

        Flux<Message> messages = repository.getAllMessages();

        StepVerifier.create(messages)
                .expectComplete()
                .verify();
        verifyNoMoreInteractions(otherChannel);
    }

    @Test
    void shouldIgnoreChannelWhenCantFetchLastMessageId() {
        TextChannel channel = mock(TextChannel.class);
        when(channel.getLastMessageId()).thenReturn(Optional.empty());
        when(channel.getName()).thenReturn("channelName");
        when(channel.getType()).thenReturn(Type.GUILD_TEXT);
        when(guild.getChannels()).thenReturn(Flux.just(channel));

        Flux<Message> messages = repository.getAllMessages();

        StepVerifier.create(messages)
                .expectComplete()
                .verify();
        verify(channel).getLastMessageId();
        verifyNoMoreInteractions(channel);
        assertThat(errorStreamCaptor.toString()).contains("Not parsing channel channelName because cannot get last message id.");
    }
}