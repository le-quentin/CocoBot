package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImpersonateCommandTest {

 @Test
 void shouldApply() {
  User user = mock(User.class);
  Impersonator impersonator = mock(Impersonator.class);
  when(impersonator.impersonate(user)).thenReturn("impersonation");
  ImpersonateCommand command = new ImpersonateCommand(impersonator, user);

  Optional<BotMessage> reply = command.apply();

  assertThat(reply)
          .usingRecursiveComparison()
          .isEqualTo(Optional.of(new BotMessage("impersonation")));
 }

 @Test
 void shouldApplyWhenUserNotFound() {
  User user = mock(User.class);
  Impersonator impersonator = mock(Impersonator.class);
  when(impersonator.impersonate(user)).thenThrow(new UserNotFoundException("username"));
  ImpersonateCommand command = new ImpersonateCommand(impersonator, user);

  Optional<BotMessage> reply = command.apply();

  assertThat(reply)
          .usingRecursiveComparison()
          .isEqualTo(Optional.of(new BotMessage("Je ne connais pas l'utilisateur username")));
 }

}