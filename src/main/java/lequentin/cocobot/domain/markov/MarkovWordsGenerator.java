package lequentin.cocobot.domain.markov;

import lequentin.cocobot.domain.StringTokenizer;

import java.util.stream.Collectors;

public class MarkovWordsGenerator {

    private final StringTokenizer sentencesStringTokenizer;
    private final MarkovTokenizer markovTokenizer;
    private final MarkovChainsWalker<WordsTuple> walker;

    private final MarkovChains<WordsTuple> markovChains;

    public MarkovWordsGenerator(StringTokenizer sentencesStringTokenizer, MarkovTokenizer markovTokenizer, MarkovChainsWalker<WordsTuple> walker) {
        this.sentencesStringTokenizer = sentencesStringTokenizer;
        this.markovTokenizer = markovTokenizer;
        this.walker = walker;
        this.markovChains = new MarkovChains<>();
    }

    public void addText(String text) {
        sentencesStringTokenizer.tokenize(text)
                .map(markovTokenizer::tokenize)
                .map(tokens -> tokens.collect(Collectors.toList()))
                .forEach(markovTokens -> {
                    for (int i=0; i<markovTokens.size()-1; i++) {
                        markovChains.addTransition(markovTokens.get(i), markovTokens.get(i+1));
                    }
                });
    }

    public String generate() {
        MarkovPath<WordsTuple> path = walker.walkFromUntil(
                markovChains,
                WordsTuple.EMPTY,
                pathBuilder -> pathBuilder.getLastAddedState().getValue() == WordsTuple.EMPTY
        );
        return path.getPath()
                .filter(wordsTuple -> wordsTuple != WordsTuple.EMPTY)
                .map(WordsTuple::lastWord)
                .collect(Collectors.joining(" "))
                .replaceAll("< : (.*) : (.*)>", "<:$1:$2>") // TODO extract those in post-treatments decorators
                .replaceAll(" (,|:|;|/|\\))", "$1")
                .replaceAll("(/|\\() ", "$1");
    }
}
