package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.messages.ApplicationMessageProvider;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static lequentin.cocobot.application.messages.ApplicationMessageCode.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpersonateCommandTest {

    @Mock
    private ApplicationMessageProvider applicationMessageProvider;

    @Mock
    private Impersonator impersonator;

    @Mock
    private User user;

    @InjectMocks
    private ImpersonateCommand command;

    @Test
    void shouldExecute() {
        when(impersonator.impersonate(user)).thenReturn("impersonation");

        Optional<BotMessage> reply = command.execute();

        assertThat(reply)
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(new BotMessage("impersonation")));
    }

    @Test
    void shouldExecuteWhenUserNotFound() {
        when(impersonator.impersonate(user)).thenThrow(new UserNotFoundException("username"));
        when(applicationMessageProvider.getMessage(USER_NOT_FOUND, "username")).thenReturn("user not found");

        Optional<BotMessage> reply = command.execute();

        assertThat(reply)
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(new BotMessage("user not found")));
    }

}