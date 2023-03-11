package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;

import java.util.Optional;

public class CocoChatBotApplication implements ChatBot {

    private final Impersonator impersonator;
    private final CocoCommandParser commandParser;

    public CocoChatBotApplication(Impersonator impersonator, CocoCommandParser commandParser) {
        this.impersonator = impersonator;
        this.commandParser = commandParser;
    }

    public Optional<BotMessage> handleMessage(Message message) {
        Optional<Command> command = commandParser
                .parse(message);

        if (command.isEmpty()) {
            handleNonCommandMessage(message);
            return Optional.empty();
        }

        return command.map(c -> c.apply(impersonator));
    }

    private void handleNonCommandMessage(Message message) {
        System.out.println("Adding message to model: " + message.getText());
        impersonator.addMessage(message);
    }
}
