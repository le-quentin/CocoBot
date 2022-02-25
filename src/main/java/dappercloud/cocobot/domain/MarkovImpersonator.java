package dappercloud.cocobot.domain;

import dappercloud.cocobot.domain.markov.MarkovChains;
import dappercloud.cocobot.domain.markov.MarkovState;
import dappercloud.cocobot.domain.markov.MarkovTokenizer;
import dappercloud.cocobot.domain.markov.WordsTuple;

import java.util.stream.Collectors;

public class MarkovImpersonator implements Impersonator {

    private final StringTokenizer sentencesStringTokenizer;
    private final MarkovTokenizer markovTokenizer;
    private final MarkovChains<WordsTuple> markovChains;

    public MarkovImpersonator(StringTokenizer sentencesStringTokenizer, MarkovTokenizer markovTokenizer, MarkovChains<WordsTuple> markovChains) {
        this.sentencesStringTokenizer = sentencesStringTokenizer;
        this.markovTokenizer = markovTokenizer;
        this.markovChains = markovChains;
    }

    @Override
    public void addMessage(Message message) {
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
        //TODO extract in MarkovWalker/Crawler or something
        MarkovState<WordsTuple> currentState = markovChains
                .getState(WordsTuple.EMPTY)
                .electNext();
        StringBuilder builder = new StringBuilder(currentState.getValue().join(" "));
        currentState = currentState.electNext();
        while (!currentState.getValue().equals(WordsTuple.EMPTY)) {
            builder.append(" ").append(currentState.getValue().lastWord());
            currentState = currentState.electNext();
        }
        return builder.toString();
    }
}
