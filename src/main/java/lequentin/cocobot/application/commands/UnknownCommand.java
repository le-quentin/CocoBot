package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.Command;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.MessageReply;

public class UnknownCommand implements Command {
    @Override
    public MessageReply apply(Impersonator impersonator) {
        return new MessageReply("Je ne connais pas cette commande !");
    }
}
