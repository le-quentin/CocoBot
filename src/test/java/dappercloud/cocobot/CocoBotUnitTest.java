package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocoBotUnitTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private Impersonator impersonator;

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
    void shouldHandleCMeCommand() {
        User user = mock(User.class);
        Message message = mockMessageWithContent("c/me");
        when(message.getAuthor()).thenReturn(Optional.of(user));
        when(impersonator.impersonate(user)).thenReturn("an impersonation");

        coco.handleMessage(message);

        verify(messageClient).replyToMessage(message, "an impersonation");
    }

    @Test
    void shouldHandleCMeCommandWhenMessageHasNoAuthor() {
        Message message = mockMessageWithContent("c/me");
        when(message.getAuthor()).thenReturn(Optional.empty());

        coco.handleMessage(message);

        verify(messageClient).replyToMessage(message, "Y a un problème avec Discord, koâââ koââ");
        verifyNoInteractions(impersonator);
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

        assertThat(outputStreamCaptor.toString()).contains("Adding message to model: Random message");
        verify(impersonator).addToModel(message);
        verifyNoInteractions(messageClient);
    }

    private Message mockMessageWithContent(String content) {
        Message message = mock(Message.class);
        when(message.getContent()).thenReturn(content);
        return message;
    }
}