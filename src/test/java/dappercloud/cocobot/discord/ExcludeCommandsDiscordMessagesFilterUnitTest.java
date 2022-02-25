package dappercloud.cocobot.discord;

import dappercloud.cocobot.domain.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class ExcludeCommandsDiscordMessagesFilterUnitTest {

    private ExcludeCommandsDiscordMessagesFilter filter = new ExcludeCommandsDiscordMessagesFilter();

    @ValueSource(strings = {
            "A perfectly fine message",
            "cc/dd", "Yes sure!"
    })
    @ParameterizedTest
    void shouldFilter(Message msg) {
        assertThat(filter.accepts(msg)).isTrue();
    }

    @ValueSource(strings = {
            "t/a command", "s/s/s////", "/ whatever command", "///",
            "f!a command", "f!/! s", "! whatever command", "!!!"
    })
    @ParameterizedTest
    void shouldFilterOut(Message msg) {
        assertThat(filter.accepts(msg)).isFalse();
    }

}