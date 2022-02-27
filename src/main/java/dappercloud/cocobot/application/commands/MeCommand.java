package dappercloud.cocobot.application.commands;

import dappercloud.cocobot.application.Command;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MessageReply;
import dappercloud.cocobot.domain.User;

public class MeCommand implements Command {

    private final User author;

    public MeCommand(User author) {
        this.author = author;
    }

    @Override
    public MessageReply apply(Impersonator impersonator) {
        return new MessageReply(impersonator.impersonate(author));
    }
}
