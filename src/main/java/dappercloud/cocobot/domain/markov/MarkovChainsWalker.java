package dappercloud.cocobot.domain.markov;

import dappercloud.cocobot.domain.markov.MarkovPath.Builder;

import java.util.function.Predicate;

public interface MarkovChainsWalker<T> {
    MarkovPath<T> walkFromUntil(MarkovChains<T> markovChains, T startingPoint, Predicate<Builder<T>> walkUntil);
}
