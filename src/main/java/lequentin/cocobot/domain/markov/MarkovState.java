package lequentin.cocobot.domain.markov;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MarkovState<T> {
    private final T value;

    private ConcurrentMap<MarkovState<T>, AtomicInteger> transitions;

    private int totalCount;

    public MarkovState(T value) {
        this.value = value;
        this.transitions = new ConcurrentHashMap<>();
        this.totalCount = 0;
    }

    public void incrementTransitionTo(MarkovState<T> otherState) {
        transitions.compute(otherState, (k,v) -> v==null ? new AtomicInteger(1) : new AtomicInteger(v.incrementAndGet()));
        totalCount++;
    }

    public MarkovState<T> electNext(Random random) {
        int randomInt = random.nextInt(totalCount);
        int cumul = 0;
        List<Entry<MarkovState<T>, AtomicInteger>> sortedStates = transitions.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.comparingInt(AtomicInteger::get).reversed()))
                .toList();
        for(var entry : sortedStates) {
            cumul += entry.getValue().get();
            if (randomInt < cumul) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Should not have reached here!");
    }

    public T getValue() {
        return value;
    }

    public int nextStatesCount() {
        return transitions.size();
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

    @Override
    public String toString() {
        return "MarkovState{" +
                value + transitions.entrySet().stream()
                    .map(entry -> "-"+entry.getValue()+"->"+entry.getKey().getValue())
                    .collect(Collectors.joining(", ")) +
                '}';
    }
}
