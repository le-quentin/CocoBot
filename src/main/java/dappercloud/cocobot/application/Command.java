package dappercloud.cocobot.application;

import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MessageReply;

@FunctionalInterface
public interface Command {
    MessageReply apply(Impersonator impersonator);
}
