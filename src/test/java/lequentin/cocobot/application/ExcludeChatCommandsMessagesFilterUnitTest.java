package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExcludeChatCommandsMessagesFilterUnitTest {

    private final ExcludeChatCommandsMessagesFilter filter = new ExcludeChatCommandsMessagesFilter();

    @ValueSource(strings = {
            "A perfectly fine message",
            "cc/dd", "Yes sure!"
    })
    @ParameterizedTest
    void shouldFilter(String msg) {
        assertThat(filter.accepts(fromString(msg))).isTrue();
    }

    @ValueSource(strings = {
            "t/a command", "s/s/s////", "/ whatever command", "///",
            "f!a command", "f!/! s", "! whatever command", "!!!"
    })
    @ParameterizedTest
    void shouldFilterOut(String msg) {
        assertThat(filter.accepts(fromString(msg))).isFalse();
    }

    private Message fromString(String str) {
        Message msg = mock(Message.class);
        when(msg.getText()).thenReturn(str);
        return msg;
    }
}