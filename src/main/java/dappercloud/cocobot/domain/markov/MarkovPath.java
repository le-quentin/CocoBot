package dappercloud.cocobot.domain.markov;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MarkovPath<T> {

    private final Stream<T> path;
    private final int nonDeterministicScore;

    private MarkovPath(Builder<T> builder) {
       this.path = builder.states.stream()
               .map(MarkovState::getValue) ;
       this.nonDeterministicScore = builder.nonDeterministicScore;
    }

    public Stream<T> getPath() {
        return path;
    }

    public int getNonDeterministicScore() {
        return nonDeterministicScore;
    }

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static final class Builder<T> {
        private List<MarkovState<T>> states;
        private int nonDeterministicScore;
        private MarkovState<T> lastAddedState;

        public Builder() {
            this.states = new ArrayList<>();
            this.nonDeterministicScore = 0;
        }

        public Builder<T> nextState(MarkovState<T> state) {
            states.add(state);
            nonDeterministicScore += Optional.ofNullable(lastAddedState)
                    .map(lastTate -> state.nextStatesCount() - 1)
                    .orElse(0);
            lastAddedState = state;
            return this;
        }

        public int getNonDeterministicScore() {
            return nonDeterministicScore;
        }

        public MarkovState<T> getLastAddedState() {
            return lastAddedState;
        }

        public MarkovPath<T> build() {
            return new MarkovPath<T>(this);
        }
    }
}
