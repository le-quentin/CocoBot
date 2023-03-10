package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;

@FunctionalInterface
public interface Command {
    BotMessage apply(Impersonator impersonator);
}
