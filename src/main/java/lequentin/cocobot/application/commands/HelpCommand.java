package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.Command;
import lequentin.cocobot.application.messages.ApplicationMessageProvider;

import java.util.Optional;

import static lequentin.cocobot.application.messages.ApplicationMessageCode.HELP;

public class HelpCommand implements Command {

    private final ApplicationMessageProvider applicationMessageProvider;

    public HelpCommand(ApplicationMessageProvider applicationMessageProvider) {
        this.applicationMessageProvider = applicationMessageProvider;
    }

    @Override
    public Optional<BotMessage> apply() {
        return Optional.of(new BotMessage(applicationMessageProvider.getMessage(HELP)));
    }
}
