package lequentin.cocobot.application;

import lequentin.cocobot.application.commands.ImpersonateCommand;
import lequentin.cocobot.application.commands.RegisterMessageCommand;
import lequentin.cocobot.application.commands.UnknownCommand;
import lequentin.cocobot.application.messages.ApplicationMessageProvider;
import lequentin.cocobot.config.Config;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class CocoCommandParser {

    private final String prefix;
    private final Impersonator impersonator;
    private final ApplicationMessageProvider applicationMessageProvider;

    public CocoCommandParser(Config config, Impersonator impersonator, ApplicationMessageProvider applicationMessageProvider) {
        this.prefix = config.getPrefix();
        this.impersonator = impersonator;
        this.applicationMessageProvider = applicationMessageProvider;
    }

    public Optional<Command> parse(Message message) {
        String text = message.getText();
        if (!text.startsWith(prefix)) return Optional.of(new RegisterMessageCommand(impersonator, message));

        String[] args = text.substring(prefix.length()).split(" ");

        Command command = switch(args[0]) {
            case "me" -> new ImpersonateCommand(applicationMessageProvider, impersonator, message.getAuthor());
            case "like" -> impersonateCommandFromArgs(args);
            default -> new UnknownCommand(applicationMessageProvider);
        };

        return Optional.of(command);
    }

    private ImpersonateCommand impersonateCommandFromArgs(String[] args) {
        String username = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        User userToImpersonate = new User(username);
        return new ImpersonateCommand(applicationMessageProvider, impersonator, userToImpersonate);
    }
}
