package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.MessageReply;

@FunctionalInterface
public interface Command {
    MessageReply apply(Impersonator impersonator);
}
