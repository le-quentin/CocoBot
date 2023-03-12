package lequentin.cocobot.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BotMessageTest {

    @Test
    void shouldNotCreateWithBlankText() {
        assertThatThrownBy(() -> new BotMessage("  "))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not be blank");
    }

}