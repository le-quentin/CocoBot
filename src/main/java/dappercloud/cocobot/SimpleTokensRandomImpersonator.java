package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleTokensRandomImpersonator implements Impersonator {

    private final Tokenizer tokenizer;
    private final Random random;
    private final Map<User, List<String>> usersTokens;

    public SimpleTokensRandomImpersonator(Tokenizer tokenizer, Random random) {
        this.tokenizer = tokenizer;
        this.random = random;
        this.usersTokens = new HashMap<>();
    }

    @Override
    public void addAllMessagesFromSource(MessagesSource messagesSource) {
        messagesSource.getAllMessages().subscribe(this::addMessage);
    }

    @Override
    public void addMessage(Message message) {
        Optional<User> author = message.getAuthor();
        if (author.isPresent()) {
            List<String> currentUserTokens = usersTokens.getOrDefault(author.get(), new ArrayList<>());
            currentUserTokens.addAll(tokenizer.tokenize(message.getContent()).collect(Collectors.toList()));
            usersTokens.put(author.get(), currentUserTokens);
        } else {
            System.out.println("Not parsing message [" + message.getContent() + " because it has not author");
        }
    }

    @Override
    public String impersonate(User user) {
        if (!usersTokens.containsKey(user)) {
            throw new UserNotFoundException("User " + user.getUsername() + " not found.");
        }

        List<String> sentences = usersTokens.get(user);
        if (sentences.isEmpty()) {
            throw new UserNotFoundException("User " + user.getUsername() + " has no messages.");
        }

        return IntStream.generate(() -> random.nextInt(sentences.size()))
                .limit(5)
                .mapToObj(sentences::get)
                .collect(Collectors.joining(". "));
    }
}
