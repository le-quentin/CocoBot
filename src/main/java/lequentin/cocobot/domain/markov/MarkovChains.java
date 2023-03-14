package lequentin.cocobot.domain.markov;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkovChains<T> {
    private final Map<T, MarkovState<T>> states;

    public MarkovChains() {
        this.states = new HashMap<>();
    }

    public void addTransition(T from, T to) {
        MarkovState<T> stateFrom = getOrCreateState(from);
        MarkovState<T> stateTo = getOrCreateState(to);
        stateFrom.incrementTransitionTo(stateTo);
    }

    private MarkovState<T> getOrCreateState(T value) {
       return states.computeIfAbsent(value, k -> new MarkovState<>(value));
    }

    public MarkovState<T> getState(T value) {
        return states.get(value);
    }

    public Metadata getMetadata() {
        List<Integer> nextStatesCounts = states.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(WordsTuple.EMPTY))
                .mapToInt(entry -> entry.getValue().nextStatesCount())
                .boxed()
                .toList();

        double avg = nextStatesCounts.stream().mapToInt(i -> i).average().orElse(0);
        return new Metadata(
                avg,
                nextStatesCounts.stream().mapToDouble(i -> Math.pow((double)i - avg, 2)).average().orElse(0),
                nextStatesCounts.stream().filter(i -> i == 1).count(),
                nextStatesCounts.stream().filter(i -> i == 2).count(),
                nextStatesCounts.stream().filter(i -> i >= 3).count()
        );
    }

    record Metadata(
            double nextStatesAverage,
            double nextStatesVariance,
            long oneNextStateCount,
            long twoNextStatesCount,
            long threeOrMoreNextStatesCount) {
    }
}
