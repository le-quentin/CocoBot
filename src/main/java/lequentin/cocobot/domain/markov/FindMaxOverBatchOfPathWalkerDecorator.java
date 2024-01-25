package lequentin.cocobot.domain.markov;

import lequentin.cocobot.domain.markov.MarkovPath.Builder;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FindMaxOverBatchOfPathWalkerDecorator<T> implements MarkovChainsWalker<T> {

    private final MarkovChainsWalker<T> walker;
    private final Comparator<MarkovPath<T>> pathsComparator;
    private final int numberOfPaths;
    private final int skip;

    public FindMaxOverBatchOfPathWalkerDecorator(MarkovChainsWalker<T> walker, Comparator<MarkovPath<T>> pathsComparator, int numberOfPaths, int skip) {
        this.walker = walker;
        this.pathsComparator = pathsComparator;
        this.numberOfPaths = numberOfPaths;
        this.skip = skip;
    }

    @Override
    public MarkovPath<T> walkFromUntil(MarkovChains<T> markovChains, T startingPoint, Predicate<Builder<T>> walkUntil) {
        return IntStream.range(0, numberOfPaths)
                .mapToObj(i -> walker.walkFromUntil(markovChains, startingPoint, walkUntil))
                .sorted(pathsComparator)
                .skip(skip)
                .max(pathsComparator)
                .orElseThrow(() -> new RuntimeException("Should never happen!"));
    }

    private String getSentence(MarkovPath<WordsTuple> path) {
        return Stream.concat(Stream.of(), path.getPath())
                .filter(tuple -> tuple != WordsTuple.EMPTY)
                .map(WordsTuple::lastWord).collect(Collectors.joining(" "));
    }
}
