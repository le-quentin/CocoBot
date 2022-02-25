package dappercloud.cocobot.domain.markov;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
