package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.Command;
import lequentin.cocobot.application.BotMessage;

import java.util.Optional;

public class UnknownCommand implements Command {
    @Override
    public Optional<BotMessage> apply() {
        return Optional.of(new BotMessage("Je ne connais pas cette commande !"));
    }
}
