package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.messages.ApplicationMessageCode;
import lequentin.cocobot.application.messages.ApplicationMessageProvider;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpCommandTest {
    @Test
    void shouldExecute() {
        ApplicationMessageProvider applicationMessageProvider = mock(ApplicationMessageProvider.class);
        when(applicationMessageProvider.getMessage(ApplicationMessageCode.HELP)).thenReturn("help message");
        HelpCommand command = new HelpCommand(applicationMessageProvider);

        Optional<BotMessage> result = command.execute();

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(Optional.of(new BotMessage("help message")));
    }
}