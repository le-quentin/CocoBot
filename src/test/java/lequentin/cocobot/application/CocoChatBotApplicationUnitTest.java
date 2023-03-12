package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CocoChatBotApplicationUnitTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private CocoCommandParser commandParser;

    @InjectMocks
    private CocoChatBotApplication coco;

    @Test
    void shouldNotHandleMessageWithoutMessage() {
        assertThatThrownBy(() -> coco.handleMessage(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldHandleMessage() {
        IncomingMessage incomingMessage = mock(IncomingMessage.class);
        Message message = mock(Message.class);
        when(incomingMessage.toDomain()).thenReturn(message);
        Command command = mock(Command.class);
        BotMessage reply = mock(BotMessage.class);
        when(commandParser.parse(message)).thenReturn(Optional.of(command));
        when(command.apply()).thenReturn(Optional.of(reply));

        coco.handleMessage(incomingMessage);

        verify(incomingMessage).reply(reply);
    }

}