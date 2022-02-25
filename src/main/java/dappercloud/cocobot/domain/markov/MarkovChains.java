package dappercloud.cocobot.domain.markov;

import java.util.HashMap;
import java.util.Map;

public class MarkovChains<T> {
    private final Map<T, MarkovState<T>> states;

    public MarkovChains() {
        this.states = new HashMap<>();
    }

    public void addTransition(T from, T to) {
        MarkovState<T> stateFrom = getState(from);
        MarkovState<T> stateTo = getState(to);
        stateFrom.incrementTransitionTo(stateTo);
    }

    private MarkovState<T> getState(T value) {
       if (!states.containsKey(value)) states.put(value, new MarkovState<>(value));
       return states.get(value);
    }
}
