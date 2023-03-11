package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.Command;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.UserNotFoundException;

import java.util.Optional;

public class LikeCommand implements Command {

    private final User author;

    public LikeCommand(User author) {
        this.author = author;
    }

    @Override
    public BotMessage apply(Impersonator impersonator) {
        try {
            return new BotMessage(impersonator.impersonate(author));
        } catch (UserNotFoundException ex) {
            return new BotMessage("Je ne connais pas l'utilisateur " + ex.getUsername());
        }
    }
}
