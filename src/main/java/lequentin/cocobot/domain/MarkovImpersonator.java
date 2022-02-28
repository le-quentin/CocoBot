package lequentin.cocobot.domain;

import lequentin.cocobot.domain.markov.MarkovChains;
import lequentin.cocobot.domain.markov.MarkovChainsWalker;
import lequentin.cocobot.domain.markov.MarkovPath;
import lequentin.cocobot.domain.markov.MarkovTokenizer;
import lequentin.cocobot.domain.markov.WordsTuple;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MarkovImpersonator implements Impersonator {

    private final StringTokenizer sentencesStringTokenizer;
    private final MarkovTokenizer markovTokenizer;
    private final MarkovChainsWalker<WordsTuple> walker;

    private final Map<User, MarkovChains<WordsTuple>> userMarkovChains;

    public MarkovImpersonator(StringTokenizer sentencesStringTokenizer, MarkovTokenizer markovTokenizer, MarkovChainsWalker<WordsTuple> walker) {
        this.sentencesStringTokenizer = sentencesStringTokenizer;
        this.markovTokenizer = markovTokenizer;
        this.walker = walker;
        this.userMarkovChains = new HashMap<>();
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
        MarkovPath<WordsTuple> path = walker.walkFromUntil(
                userMarkovChains.get(user),
                WordsTuple.EMPTY,
                pathBuilder -> pathBuilder.getLastAddedState().getValue() == WordsTuple.EMPTY
        );
        return path.getPath()
                .filter(wordsTuple -> wordsTuple != WordsTuple.EMPTY)
                .map(WordsTuple::lastWord)
                .collect(Collectors.joining(" "));
    }

    private MarkovChains<WordsTuple> getOrCreateUserChains(User user) {
        if (!userMarkovChains.containsKey(user)) userMarkovChains.put(user, new MarkovChains<>());
        return userMarkovChains.get(user);
    }

}
