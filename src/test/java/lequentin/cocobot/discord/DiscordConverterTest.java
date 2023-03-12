package lequentin.cocobot.discord;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DiscordConverterTest {

    private final DiscordConverter converter = new DiscordConverter();

    @Test
    void shouldNotConvertMessageToDomainWhenMessageHasNoAuthor() {
        Message discordMessage = mock(Message.class);

        assertThatThrownBy(() -> converter.toDomain(discordMessage))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("has no user");
    }

    @Test
    void shouldConvertMessageToDomain() {
        User discordUser = mock(User.class);
        when(discordUser.getUsername()).thenReturn("messageAuthor");
        Message discordMessage = mock(Message.class);
        when(discordMessage.getAuthor()).thenReturn(Optional.of(discordUser));
        when(discordMessage.getTimestamp()).thenReturn(Instant.MIN);
        when(discordMessage.getContent()).thenReturn("message content");

        lequentin.cocobot.domain.Message result = converter.toDomain(discordMessage);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(new lequentin.cocobot.domain.Message(
                        new lequentin.cocobot.domain.User("messageAuthor"),
                        Instant.MIN,
                        "message content"
                ));
    }

}