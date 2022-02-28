package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessageReply;
import lequentin.cocobot.domain.UserNotFoundException;

import java.util.Optional;

public class CocoChatBotApplication implements ChatBot {

    private final Impersonator impersonator;
    private final CocoCommandParser commandParser;

    public CocoChatBotApplication(Impersonator impersonator, CocoCommandParser commandParser) {
        this.impersonator = impersonator;
        this.commandParser = commandParser;
    }

    public Optional<MessageReply> handleMessage(Message message) {
        Optional<Command> command = commandParser
                .parse(message);

        if (command.isEmpty()) {
            handleNonCommandMessage(message);
            return Optional.empty();
        }

        try {
            return command.map(c -> c.apply(impersonator));
        } catch (UserNotFoundException ex) {
            return Optional.of(new MessageReply("Je ne connais pas l'utilisateur " + ex.getUsername()));
        }
    }

    private void handleNonCommandMessage(Message message) {
        System.out.println("Adding message to model: " + message.getText());
        impersonator.addMessage(message);
    }
}
