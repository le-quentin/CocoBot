package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.Command;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.application.BotMessage;

public class UnknownCommand implements Command {
    @Override
    public BotMessage apply(Impersonator impersonator) {
        return new BotMessage("Je ne connais pas cette commande !");
    }
}
