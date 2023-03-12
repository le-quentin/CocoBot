package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterMessageCommandTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void shouldApply() {
        Message message = mock(Message.class);
        Impersonator impersonator = mock(Impersonator.class);
        when(message.getText()).thenReturn("Random message");
        RegisterMessageCommand command = new RegisterMessageCommand(impersonator, message);

        Optional<BotMessage> reply = command.apply();

        verify(impersonator).addMessage(message);
        assertThat(outputStreamCaptor.toString()).contains("Adding message to model: Random message");
        assertThat(reply).isEmpty();
    }

}