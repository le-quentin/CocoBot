package lequentin.cocobot.application.commands;

import lequentin.cocobot.application.BotMessage;
import lequentin.cocobot.application.Command;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;

import java.util.Optional;

public class RegisterMessageCommand implements Command {

    private final Impersonator impersonator;
    private final Message message;

    public RegisterMessageCommand(Impersonator impersonator, Message message) {
        this.impersonator = impersonator;
        this.message = message;
    }

    @Override
    public Optional<BotMessage> execute() {
        System.out.println("Adding message to model: " + message.getText());
        impersonator.addMessage(message);
        return Optional.empty();
    }
}
