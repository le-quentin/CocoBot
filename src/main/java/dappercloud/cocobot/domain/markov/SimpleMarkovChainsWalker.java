package dappercloud.cocobot.domain.markov;

import java.util.Random;
import java.util.function.Predicate;

public class SimpleMarkovChainsWalker<T> implements MarkovChainsWalker<T> {

    private final Random random;

    public SimpleMarkovChainsWalker(Random random) {
        this.random = random;
    }

    //TODO add a security net `maxIterations` argument or something similar
    public MarkovPath<T> walkFromUntil(MarkovChains<T> markovChains, T startingPoint, Predicate<MarkovPath.Builder<T>> walkUntil) {
        MarkovPath.Builder<T> builder = MarkovPath.builder();
        MarkovState<T> currentState = markovChains.getState(startingPoint);
        builder.nextState(currentState);

        do {
            currentState = currentState.electNext(random);
            builder.nextState(currentState);
        } while(!walkUntil.test(builder));

            return builder.build();
    }
}
