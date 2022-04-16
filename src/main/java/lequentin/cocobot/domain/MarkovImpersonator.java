package lequentin.cocobot.domain;

import lequentin.cocobot.domain.markov.MarkovChainsWalker;
import lequentin.cocobot.domain.markov.MarkovTokenizer;
import lequentin.cocobot.domain.markov.MarkovWordsGenerator;
import lequentin.cocobot.domain.markov.WordsTuple;

import java.util.HashMap;
import java.util.Map;

public class MarkovImpersonator implements Impersonator {

    private final StringTokenizer sentencesStringTokenizer;
    private final MarkovTokenizer markovTokenizer;
    private final MarkovChainsWalker<WordsTuple> walker;

    private final Map<User, MarkovWordsGenerator> userMarkovGenerators;

    public MarkovImpersonator(StringTokenizer sentencesStringTokenizer, MarkovTokenizer markovTokenizer, MarkovChainsWalker<WordsTuple> walker) {
        this.sentencesStringTokenizer = sentencesStringTokenizer;
        this.markovTokenizer = markovTokenizer;
        this.walker = walker;
        this.userMarkovGenerators = new HashMap<>();
    }

    @Override
    public void addMessage(Message message) {
        getOrCreateUserGenerator(message.getAuthor())
                .addText(message.getText());
    }

    @Override
    public String impersonate(User user) {
        if (!userMarkovGenerators.containsKey(user)) {
            throw new UserNotFoundException(user.getUsername());
        }
        return userMarkovGenerators.get(user)
                .generate();
    }

    private MarkovWordsGenerator getOrCreateUserGenerator(User user) {
        if (!userMarkovGenerators.containsKey(user)) userMarkovGenerators.put(user, newMarkovGenerator());
        return userMarkovGenerators.get(user);
    }

    private MarkovWordsGenerator newMarkovGenerator() {
        return new MarkovWordsGenerator(sentencesStringTokenizer, markovTokenizer, walker);
    }

}
