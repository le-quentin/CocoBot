package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.Command;
import lequentin.cocobot.application.messages.ApplicationMessageProvider;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.UserNotFoundException;

import java.util.Optional;

import static lequentin.cocobot.application.messages.ApplicationMessageCode.USER_NOT_FOUND;

public class ImpersonateCommand implements Command {

    private final ApplicationMessageProvider applicationMessageProvider;
    private final Impersonator impersonator;
    private final User author;

    public ImpersonateCommand(ApplicationMessageProvider applicationMessageProvider, Impersonator impersonator, User author) {
        this.applicationMessageProvider = applicationMessageProvider;
        this.impersonator = impersonator;
        this.author = author;
    }

    @Override
    public Optional<BotMessage> execute() {
        try {
            return Optional.of(new BotMessage(impersonator.impersonate(author)));
        } catch (UserNotFoundException ex) {
            String reply = applicationMessageProvider.getMessage(USER_NOT_FOUND, ex.getUsername());
            return Optional.of(new BotMessage(reply));
        }
    }
}
