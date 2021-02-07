package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageClientUnitTest {

    private MessageClient messageClient = new MessageClient();
    private MessageChannel channel;

    @BeforeEach
    void setUp() {
        channel = mockChannel();
    }

    @Test
    void shouldNotReplyToMessageWithoutMessage() {
        assertThatThrownBy(() -> messageClient.replyToMessage(null, "reply"))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldNotReplyToMessageWithoutReply() {
        assertThatThrownBy(() -> messageClient.replyToMessage(mockMessageInChannel(channel), null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldNotReplyToMessageWithBlankReply() {
        assertThatThrownBy(() -> messageClient.replyToMessage(mockMessageInChannel(channel), "  "))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reply should not be blank");
    }

    @Test
    void shouldReplyToMessage() {
        MessageChannel channel = mockChannel();
        Message message = mockMessageInChannel(channel);

        messageClient.replyToMessage(message, "reply");

        verify(channel).createMessage("reply");
    }

    private MessageChannel mockChannel() {
        MessageChannel channel = mock(MessageChannel.class);
        when(channel.createMessage(anyString())).thenReturn(Mono.just(mock(Message.class)));
        return channel;
    }

    private Message mockMessageInChannel(MessageChannel channel) {
        Message message = mock(Message.class);
        when(message.getChannel()).thenReturn(Mono.just(channel));
        return message;
    }
}