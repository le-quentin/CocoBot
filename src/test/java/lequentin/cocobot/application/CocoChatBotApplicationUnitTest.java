package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void shouldHandleMessageWithCommand() {
        IncomingMessage incomingMessage = mock(IncomingMessage.class);
        Message message = mock(Message.class);
        when(incomingMessage.toDomain()).thenReturn(message);
        Command command = mock(Command.class);
        BotMessage reply = mock(BotMessage.class);
        when(commandParser.parse(message)).thenReturn(Optional.of(command));
        when(command.apply(impersonator)).thenReturn(reply);

        coco.handleMessage(incomingMessage);

        verify(incomingMessage).reply(reply);
    }

    @Test
    void shouldHandleMessageWithNonCommandMessage() {
        IncomingMessage incomingMessage = mock(IncomingMessage.class);
        Message message = mock(Message.class);
        when(incomingMessage.toDomain()).thenReturn(message);
        when(message.getText()).thenReturn("Random message");
        when(commandParser.parse(message)).thenReturn(Optional.empty());

        coco.handleMessage(incomingMessage);

        verify(impersonator).addMessage(message);
        assertThat(outputStreamCaptor.toString()).contains("Adding message to model: Random message");
        verify(incomingMessage, never()).reply(any());
    }
}