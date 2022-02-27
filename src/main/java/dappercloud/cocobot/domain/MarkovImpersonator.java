package dappercloud.cocobot.domain;

import dappercloud.cocobot.domain.markov.MarkovChains;
import dappercloud.cocobot.domain.markov.MarkovState;
import dappercloud.cocobot.domain.markov.MarkovTokenizer;
import dappercloud.cocobot.domain.markov.WordsTuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class MarkovImpersonator implements Impersonator {

    private final StringTokenizer sentencesStringTokenizer;
    private final MarkovTokenizer markovTokenizer;

    private final Map<User, MarkovChains<WordsTuple>> userMarkovChains;
    private final Random random;

    public MarkovImpersonator(StringTokenizer sentencesStringTokenizer, MarkovTokenizer markovTokenizer, MarkovChains<WordsTuple> markovChains, Random random) {
        this.sentencesStringTokenizer = sentencesStringTokenizer;
        this.markovTokenizer = markovTokenizer;
        this.userMarkovChains = new HashMap<>();
        this.random = random;
    }

    @Override
    public void addMessage(Message message) {
        MarkovChains<WordsTuple> markovChains = getOrCreateUserChains(message.getAuthor());
        sentencesStringTokenizer.tokenize(message.getText())
                .map(markovTokenizer::tokenize)
                .map(tokens -> tokens.collect(Collectors.toList()))
                .forEach(markovTokens -> {
                    for (int i=0; i<markovTokens.size()-1; i++) {
                        markovChains.addTransition(markovTokens.get(i), markovTokens.get(i+1));
                    }
                });
    }

    @Override
    public String impersonate(User user) {
        if (!userMarkovChains.containsKey(user)) {
            throw new UserNotFoundException(user.getUsername());
        }
        MarkovChains<WordsTuple> markovChains = userMarkovChains.get(user);
        MarkovState<WordsTuple> currentState = markovChains
                .getState(WordsTuple.EMPTY)
                .electNext(random);
        StringBuilder builder = new StringBuilder(currentState.getValue().join(" "));
        currentState = currentState.electNext(random);
        while (!currentState.getValue().equals(WordsTuple.EMPTY)) {
            builder.append(" ").append(currentState.getValue().lastWord());
            currentState = currentState.electNext(random);
        }
        return builder.toString();
    }

    private MarkovChains<WordsTuple> getOrCreateUserChains(User user) {
        if (!userMarkovChains.containsKey(user)) userMarkovChains.put(user, new MarkovChains<>());
        return userMarkovChains.get(user);
    }

}
