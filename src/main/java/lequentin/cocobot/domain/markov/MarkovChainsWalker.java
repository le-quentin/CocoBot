package lequentin.cocobot.domain.markov;

import lequentin.cocobot.domain.markov.MarkovPath.Builder;

import java.util.function.Predicate;

public interface MarkovChainsWalker<T> {
    MarkovPath<T> walkFromUntil(MarkovChains<T> markovChains, T startingPoint, Predicate<Builder<T>> walkUntil);
}
