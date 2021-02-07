import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CocoBotUnitTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private MessageChannel channel;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        channel = mockChannel();
    }

    @Test
    void shouldNotHandleMessageWithoutMessage() {
        assertThatThrownBy(() -> new CocoBot().handleMessage(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldHandleMessageWithCommand() {
        Message message = mockMessageInChannelWithContent(channel, "c/me");
        CocoBot coco = new CocoBot();

        coco.handleMessage(message);

        verify(channel).createMessage("Message rigolo");
    }

    @Test
    void shouldHandleMessageWithUnknownCommand() {
        Message message = mockMessageInChannelWithContent(channel, "c/unknown");
        CocoBot coco = new CocoBot();

        coco.handleMessage(message);

        verify(channel).createMessage("Je ne connais pas cette commande");
    }

    @Test
    void shouldHandleMessageWithNonCommandMessage() {
        Message message = mockMessageInChannelWithContent(channel, "Random message");
        CocoBot coco = new CocoBot();

        coco.handleMessage(message);

        assertThat(outputStreamCaptor.toString()).contains("Parsing message: Random message");
        verifyNoInteractions(channel);
    }

    private MessageChannel mockChannel() {
        MessageChannel channel = mock(MessageChannel.class);
        when(channel.createMessage(anyString())).thenReturn(Mono.just(mock(Message.class)));
        return channel;
    }

    private Message mockMessageInChannelWithContent(MessageChannel channel, String content) {
        Message message = mock(Message.class);
        when(message.getContent()).thenReturn(content);
        when(message.getChannel()).thenReturn(Mono.just(channel));
        return message;
    }
}