package dappercloud.cocobot.application.commands;

import dappercloud.cocobot.application.Command;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MessageReply;

public class UnknownCommand implements Command {
    @Override
    public MessageReply apply(Impersonator impersonator) {
        return new MessageReply("Je ne connais pas cette commande !");
    }
}
