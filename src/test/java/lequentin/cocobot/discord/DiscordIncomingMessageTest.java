package lequentin.cocobot.discord;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.MessageCreateMono;
import lequentin.cocobot.application.BotMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        MessageChannel channel = mockChannel();
        Message message = mockMessageInChannel(channel);
        DiscordIncomingMessage incomingMessage = new DiscordIncomingMessage(message, discordConverter);

        incomingMessage.reply(new BotMessage("reply"));

        verify(channel).createMessage("reply");
    }

    private MessageChannel mockChannel() {
        MessageChannel channel = mock(MessageChannel.class);
        MessageCreateMono messageCreateMono = mock(MessageCreateMono.class);
        when(channel.createMessage(anyString())).thenReturn(messageCreateMono);
        return channel;
    }

    private Message mockMessageInChannel(MessageChannel channel) {
        Message message = mock(Message.class);
        when(message.getChannel()).thenReturn(Mono.just(channel));
        return message;
    }
}