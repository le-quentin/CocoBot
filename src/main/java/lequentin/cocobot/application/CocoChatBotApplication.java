package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;

import java.util.Optional;

public class CocoChatBotApplication implements ChatBot {

    private final CocoCommandParser commandParser;

    public CocoChatBotApplication(CocoCommandParser commandParser) {
        this.commandParser = commandParser;
    }

    public void handleMessage(IncomingMessage incomingMessage) {
        Message message = incomingMessage.toDomain();

        Optional<Command> command = commandParser
                .parse(message);

        Optional<BotMessage> response = command.flatMap(Command::apply);
        response.ifPresent(incomingMessage::reply);
    }

}
