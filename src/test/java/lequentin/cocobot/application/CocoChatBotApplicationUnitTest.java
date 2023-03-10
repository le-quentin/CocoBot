package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.UserNotFoundException;
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

    @Mock
    private CocoCommandParser commandParser;

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
    void shouldHandleCommand() {
        Message message = mock(Message.class);
        Command command = mock(Command.class);
        BotMessage reply = mock(BotMessage.class);
        when(commandParser.parse(message)).thenReturn(Optional.of(command));
        when(command.apply(impersonator)).thenReturn(reply);

        Optional<BotMessage> result = coco.handleMessage(message);

        assertThat(result).contains(reply);
    }

    @Test
    void shouldHandleCommandWhenUserNotFound() {
        Message message = mock(Message.class);
        Command command = mock(Command.class);
        when(commandParser.parse(message)).thenReturn(Optional.of(command));
        when(command.apply(impersonator)).thenThrow(new UserNotFoundException("username"));

        Optional<BotMessage> result = coco.handleMessage(message);

        assertThat(result)
                .usingFieldByFieldValueComparator()
                .contains(new BotMessage("Je ne connais pas l'utilisateur username"));
    }

    @Test
    void shouldHandleMessageWithNonCommandMessage() {
        Message message = mock(Message.class);
        when(message.getText()).thenReturn("Random message");
        when(commandParser.parse(message)).thenReturn(Optional.empty());

        Optional<BotMessage> result = coco.handleMessage(message);

        assertThat(result).isEmpty();
        verify(impersonator).addMessage(message);
        assertThat(outputStreamCaptor.toString()).contains("Adding message to model: Random message");
    }
}