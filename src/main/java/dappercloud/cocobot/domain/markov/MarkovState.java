package dappercloud.cocobot.domain.markov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MarkovState<T> {
    private final T value;

    private Map<MarkovState<T>, Integer> transitions;

    private int totalCount;

    public MarkovState(T value) {
        this.value = value;
        this.transitions = new HashMap<>();
        this.totalCount = 0;
    }

    public void incrementTransitionTo(MarkovState<T> otherState) {
        int count = transitions.getOrDefault(otherState, 0);
        transitions.put(otherState, count+1);
        totalCount++;
    }

    private Random rnd = new Random();
    public MarkovState<T> electNext() {
        // TODO implement properly, by computing a sorted list with cumulative values
        int index = rnd.nextInt(transitions.size());
        return new ArrayList<>(transitions.keySet()).get(index);
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkovState<?> that = (MarkovState<?>) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
