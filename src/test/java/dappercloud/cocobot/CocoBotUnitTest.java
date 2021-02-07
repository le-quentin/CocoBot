package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocoBotUnitTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private MessageClient messageClient;

    @InjectMocks
    private CocoBot coco;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void shouldNotHandleMessageWithoutMessage() {
        assertThatThrownBy(() -> coco.handleMessage(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldHandleMessageWithCommand() {
        Message message = mockMessageWithContent("c/me");

        coco.handleMessage(message);

        verify(messageClient).replyToMessage(message, "Message rigolo");
    }

    @Test
    void shouldHandleMessageWithUnknownCommand() {
        Message message = mockMessageWithContent("c/unknown");

        coco.handleMessage(message);

        verify(messageClient).replyToMessage(message, "Je ne connais pas cette commande");
    }

    @Test
    void shouldHandleMessageWithNonCommandMessage() {
        Message message = mockMessageWithContent("Random message");

        coco.handleMessage(message);

        assertThat(outputStreamCaptor.toString()).contains("Parsing message: Random message");
        verifyNoInteractions(messageClient);
    }

    private Message mockMessageWithContent(String content) {
        Message message = mock(Message.class);
        when(message.getContent()).thenReturn(content);
        return message;
    }
}