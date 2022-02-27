package dappercloud.cocobot.application;

import dappercloud.cocobot.application.commands.LikeCommand;
import dappercloud.cocobot.application.commands.MeCommand;
import dappercloud.cocobot.application.commands.UnknownCommand;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.User;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class CocoCommandParser {

    private static final String PREFIX = "c/";

    public Optional<Command> parse(Message message) {
        String text = message.getText();
        if (!text.startsWith(PREFIX)) return Optional.empty();

        String[] args = text.substring(PREFIX.length()).split(" ");

        Command command = switch(args[0]) {
            case "me" -> new MeCommand(message.getAuthor());
            case "like" -> likeCommandFromArgs(args);
            default -> new UnknownCommand();
        };

        return Optional.of(command);
    }

    private LikeCommand likeCommandFromArgs(String[] args) {
        String username = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        User userToImpersonate = new User(username);
        return new LikeCommand(userToImpersonate);
    }
}
