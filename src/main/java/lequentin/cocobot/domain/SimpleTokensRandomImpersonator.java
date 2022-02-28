package lequentin.cocobot.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleTokensRandomImpersonator implements Impersonator {

    private final StringTokenizer stringTokenizer;
    private final Random random;
    private final Map<User, List<String>> usersTokens;

    public SimpleTokensRandomImpersonator(StringTokenizer stringTokenizer, Random random) {
        this.stringTokenizer = stringTokenizer;
        this.random = random;
        this.usersTokens = new HashMap<>();
    }

    @Override
    public void addMessage(Message message) {
        User author = message.getAuthor();
        List<String> currentUserTokens = usersTokens.getOrDefault(author, new ArrayList<>());
        currentUserTokens.addAll(stringTokenizer.tokenize(message.getText()).collect(Collectors.toList()));
        usersTokens.put(author, currentUserTokens);
    }

    @Override
    public String impersonate(User user) {
        if (!usersTokens.containsKey(user)) {
            throw new UserNotFoundException(user.getUsername());
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
