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
import java.util.stream.Stream;

public class FullSentenceImpersonator implements Impersonator {

    private final Random random;
    private final Map<User, List<String>> usersSentences;

    public FullSentenceImpersonator(Random random) {
        this.random = random;
        this.usersSentences = new HashMap<>();
    }

    @Override
    public void buildModel(MessagesRepository messagesRepository) {
        messagesRepository.getAllMessages().subscribe(this::addToModel);
    }

    @Override
    public void addToModel(Message message) {
        Optional<User> author = message.getAuthor();
        if (author.isPresent()) {
            List<String> currentUserSentences = usersSentences.getOrDefault(author.get(), new ArrayList<>());
            currentUserSentences.addAll(getSentences(message));
            usersSentences.put(author.get(), currentUserSentences);
        } else {
            System.out.println("Not parsing message [" + message.getContent() + " because it has not author");
        }
    }

    @Override
    public String impersonate(User user) {
        if (!usersSentences.containsKey(user)) {
            throw new UserNotFoundException("User " + user.getUsername() + " not found.");
        }

        List<String> sentences = usersSentences.get(user);
        if (sentences.isEmpty()) {
            throw new UserNotFoundException("User " + user.getUsername() + " has no messages.");
        }

        return IntStream.generate(() -> random.nextInt(sentences.size()))
                .limit(5)
                .mapToObj(sentences::get)
                .collect(Collectors.joining(". "));
    }

    private List<String> getSentences(Message message) {
        //TODO extract sentence tokenizing in a proper service and fully test it
        return Stream.of(message.getContent().split("[.?!]+"))
                .map(String::trim)
                .filter(sentence -> !sentence.isEmpty())
                .collect(Collectors.toList());
    }
}
