package lequentin.cocobot.application;

import lequentin.cocobot.application.commands.ImpersonateCommand;
import lequentin.cocobot.application.commands.RegisterMessageCommand;
import lequentin.cocobot.application.commands.UnknownCommand;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class CocoCommandParser {

    private static final String PREFIX = "c/";

    private final Impersonator impersonator;

    public CocoCommandParser(Impersonator impersonator) {
        this.impersonator = impersonator;
    }

    public Optional<Command> parse(Message message) {
        String text = message.getText();
        if (!text.startsWith(PREFIX)) return Optional.of(new RegisterMessageCommand(impersonator, message));

        String[] args = text.substring(PREFIX.length()).split(" ");

        Command command = switch(args[0]) {
            case "me" -> new ImpersonateCommand(impersonator, message.getAuthor());
            case "like" -> impersonateCommandFromArgs(args);
            default -> new UnknownCommand();
        };

        return Optional.of(command);
    }

    private ImpersonateCommand impersonateCommandFromArgs(String[] args) {
        String username = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        User userToImpersonate = new User(username);
        return new ImpersonateCommand(impersonator, userToImpersonate);
    }
}
