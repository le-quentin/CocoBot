package dappercloud.cocobot.domain.markov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkovStateUnitTest {

    private MarkovState<String> from;
    private MarkovState<String> to1;
    private MarkovState<String> to2;
    private MarkovState<String> to3;

    @Mock
    private Random random;

    @BeforeEach
    void setUpStates() {
        from = new MarkovState<>("from");
        to1 = new MarkovState<>("to1");
        to2 = new MarkovState<>("to2");
        to3 = new MarkovState<>("to3");
        increment(from, to1, 3);
        increment(from, to2, 5);
        increment(from, to3, 2);
    }

    @DisplayName("Should elect to2 as next state when random yields an int between 0 and 4")
    @ParameterizedTest
    @ValueSource(ints = { 0, 1, 2, 3, 4 })
    void shouldElectNextReturnTo2WhenRandomBetween0And4(int randomIntResult) {
        when(random.nextInt(10)).thenReturn(randomIntResult);

        MarkovState<String> next = from.electNext(random);

        assertThat(next).isSameAs(to2);
    }

    @DisplayName("Should elect to1 as next state when random yields an int between 5 and 7")
    @ValueSource(ints = { 5, 6, 7 })
    @ParameterizedTest
    void shouldElectNextReturnTo1WhenRandomBetween5And7(int randomIntResult) {
        when(random.nextInt(10)).thenReturn(randomIntResult);

        MarkovState<String> next = from.electNext(random);

        assertThat(next).isSameAs(to1);
    }

    @DisplayName("Should elect to3 as next state when random yields an int between 8 and 9")
    @ValueSource(ints = { 8, 9 })
    @ParameterizedTest
    void shouldElectNextReturnTo3WhenRandomBetween8And9(int randomIntResult) {
        when(random.nextInt(10)).thenReturn(randomIntResult);

        MarkovState<String> next = from.electNext(random);

        assertThat(next).isSameAs(to3);
    }

    private <T> void increment(MarkovState<T> from, MarkovState<T> to, int n) {
        IntStream.range(0, n).forEach(i -> from.incrementTransitionTo(to));
    }
}