package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.Command;
import lequentin.cocobot.application.messages.ApplicationMessageProvider;

import java.util.Optional;

import static lequentin.cocobot.application.messages.ApplicationMessageCode.COMMAND_UNKNOWN;

public class UnknownCommand implements Command {

    private final ApplicationMessageProvider applicationMessageProvider;

    public UnknownCommand(ApplicationMessageProvider applicationMessageProvider) {
        this.applicationMessageProvider = applicationMessageProvider;
    }

    @Override
    public Optional<BotMessage> execute() {
        return Optional.of(new BotMessage(applicationMessageProvider.getMessage(COMMAND_UNKNOWN)));
    }
}
