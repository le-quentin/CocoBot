package lequentin.cocobot.discord;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import lequentin.cocobot.application.BotMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DiscordIncomingMessageTest {

    private DiscordConverter discordConverter;

    @BeforeEach
    void setUp() {
        discordConverter = mock(DiscordConverter.class);
    }

    @Test
    void shouldConvertToDomain() {
        Message discordMessage = mock(Message.class);
        lequentin.cocobot.domain.Message convertedMessage = mock(lequentin.cocobot.domain.Message.class);
        when(discordConverter.toDomain(discordMessage)).thenReturn(convertedMessage);
        DiscordIncomingMessage incomingMessage = new DiscordIncomingMessage(discordMessage, discordConverter);

        lequentin.cocobot.domain.Message result = incomingMessage.toDomain();

        assertThat(result).isSameAs(convertedMessage);
    }

    @Test
    void shouldReplyToMessage() {
        MessageChannel channel = mock(MessageChannel.class);
        Message message = mockMessageInChannel(channel);
        Message createdMessage = mock(Message.class);
        lequentin.cocobot.domain.Message convertedMessage = mock(lequentin.cocobot.domain.Message.class);
        when(channel.createMessage(MessageCreateSpec.builder().content("reply").build())).thenReturn(Mono.just(createdMessage));
        when(discordConverter.toDomain(any())).thenReturn(convertedMessage);
        DiscordIncomingMessage incomingMessage = new DiscordIncomingMessage(message, discordConverter);

        StepVerifier.create(incomingMessage.reply(new BotMessage("reply")))
                .expectNext(convertedMessage)
                .verifyComplete();
        //TODO find a way to make sure the mono was subscribed here
    }

    @Test
    void shouldThrowExceptionWhenReplyingThrows() {
        MessageChannel channel = mock(MessageChannel.class);
        Message message = mockMessageInChannel(channel);
        DiscordIncomingMessage incomingMessage = new DiscordIncomingMessage(message, discordConverter);
        RuntimeException exception = new RuntimeException("error");
        when(channel.createMessage(any(MessageCreateSpec.class))).thenThrow(exception);

        StepVerifier.create(incomingMessage.reply(new BotMessage("reply")))
                .expectErrorMatches(e -> e == exception)
                .verify();
    }

    private Message mockMessageInChannel(MessageChannel channel) {
        Message message = mock(Message.class);
        when(message.getChannel()).thenReturn(Mono.just(channel));
        return message;
    }
}