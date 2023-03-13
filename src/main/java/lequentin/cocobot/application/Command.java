package lequentin.cocobot.application;

import java.util.Optional;

@FunctionalInterface
public interface Command {
    Optional<BotMessage> execute();
}
