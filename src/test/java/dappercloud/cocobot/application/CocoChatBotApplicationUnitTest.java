package dappercloud.cocobot.application;

import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;
import dappercloud.cocobot.domain.User;
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
class CocoChatBotApplicationUnitTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private Impersonator impersonator;

    @InjectMocks
    private CocoChatBotApplication coco;

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
        Message message = mockMessageWithText("c/me");
        when(message.getAuthor()).thenReturn(user);
        when(impersonator.impersonate(user)).thenReturn("an impersonation");

        Optional<MessageReply> reply = coco.handleMessage(message);

        assertThat(reply)
                .usingFieldByFieldValueComparator()
                .contains(new MessageReply("an impersonation"));
    }

    @Test
    void shouldHandleMessageWithUnknownCommand() {
        Message message = mockMessageWithText("c/unknown");

        Optional<MessageReply> reply = coco.handleMessage(message);

        assertThat(reply)
                .usingFieldByFieldValueComparator()
                .contains(new MessageReply("Je ne connais pas cette commande"));
    }

    @Test
    void shouldHandleMessageWithNonCommandMessage() {
        Message message = mockMessageWithText("Random message");

        Optional<MessageReply> reply = coco.handleMessage(message);

        assertThat(outputStreamCaptor.toString()).contains("Adding message to model: Random message");
        assertThat(reply).isEmpty();
        verify(impersonator).addMessage(message);
    }

    private Message mockMessageWithText(String text) {
        Message message = mock(Message.class);
        when(message.getText()).thenReturn(text);
        return message;
    }
}