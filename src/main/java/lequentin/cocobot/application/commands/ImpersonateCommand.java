package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.Command;
import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.UserNotFoundException;

import java.util.Optional;

public class ImpersonateCommand implements Command {

    private final Impersonator impersonator;
    private final User author;

    public ImpersonateCommand(Impersonator impersonator, User author) {
        this.impersonator = impersonator;
        this.author = author;
    }

    @Override
    public Optional<BotMessage> apply() {
        try {
            return Optional.of(new BotMessage(impersonator.impersonate(author)));
        } catch (UserNotFoundException ex) {
            return Optional.of(new BotMessage("Je ne connais pas l'utilisateur " + ex.getUsername()));
        }
    }
}
