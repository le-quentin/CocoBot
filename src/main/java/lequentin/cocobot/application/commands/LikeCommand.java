package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.Command;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.domain.User;

public class LikeCommand implements Command {

    private final User author;

    public LikeCommand(User author) {
        this.author = author;
    }

    @Override
    public BotMessage apply(Impersonator impersonator) {
        return new BotMessage(impersonator.impersonate(author));
    }
}
