package lequentin.cocobot.application;

import lequentin.cocobot.application.commands.ImpersonateCommand;
import lequentin.cocobot.application.commands.RegisterMessageCommand;
import lequentin.cocobot.application.commands.UnknownCommand;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocoCommandParserUnitTest {

    @Mock
    private Impersonator impersonator;

    @Mock
    private User user;

    @Mock
    private Message message;

    @InjectMocks
    private CocoCommandParser commandParser;

    @Test
    void shouldParseRegisterMessageCommand() {
        when(message.getText()).thenReturn("Just a random message");

        Optional<Command> command = commandParser.parse(message);

        assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(new RegisterMessageCommand(impersonator, message)));
    }

    @Test
    void shouldParseMeCommand() {
        when(message.getAuthor()).thenReturn(user);
        when(message.getText()).thenReturn("c/me");

        Optional<Command> command = commandParser.parse(message);

        assertThat(command)
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(new ImpersonateCommand(impersonator, user)));
    }

    @Test
    void shouldParseLikeCommand() {
        when(message.getText()).thenReturn("c/like nick name");

        Optional<Command> command = commandParser.parse(message);

        assertThat(command)
                .usingFieldByFieldValueComparator()
                .contains(new ImpersonateCommand(impersonator, new User("nick name")));
    }

    @Test
    void ShouldParseUnknownCommand() {
        when(message.getText()).thenReturn("c/unknowncommand");

        Optional<Command> command = commandParser.parse(message);

        assertThat(command)
                .usingFieldByFieldValueComparator()
                .contains(new UnknownCommand());
    }

}