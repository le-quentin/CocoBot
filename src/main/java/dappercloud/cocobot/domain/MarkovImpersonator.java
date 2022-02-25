package dappercloud.cocobot.domain;

import dappercloud.cocobot.domain.markov.MarkovChains;

import java.util.stream.Collectors;

public class MarkovImpersonator implements Impersonator {

    private final Tokenizer sentencesTokenizer;
    private final Tokenizer markovTokenizer;
    private final MarkovChains<String> markovChains;

    public MarkovImpersonator(Tokenizer sentencesTokenizer, Tokenizer markovTokenizer, MarkovChains<String> markovChains) {
        this.sentencesTokenizer = sentencesTokenizer;
        this.markovTokenizer = markovTokenizer;
        this.markovChains = markovChains;
    }

    @Override
    public void addMessage(Message message) {
        sentencesTokenizer.tokenize(message.getText())
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
        return null;
    }
}
