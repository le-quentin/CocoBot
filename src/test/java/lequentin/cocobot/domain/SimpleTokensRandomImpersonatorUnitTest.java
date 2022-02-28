package lequentin.cocobot.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleTokensRandomImpersonatorUnitTest {

    @Mock
    private StringTokenizer stringTokenizer;

    @Mock
    private Random random;

    @InjectMocks
    private SimpleTokensRandomImpersonator impersonator;

    @Mock
    private MessagesSource messages;

    @Test
    void shouldNotImpersonateNotFoundUser() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("john_doe");

        assertThatThrownBy(() -> impersonator.impersonate(user))
                .isExactlyInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User john_doe not found");
        verifyNoInteractions(messages);
    }

    @Test
    void shouldNotImpersonateUserWithNoTokens() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("john_doe");
        Message message = mock(Message.class);
        when(message.getAuthor()).thenReturn(user);
        when(message.getText()).thenReturn("content");
        when(stringTokenizer.tokenize("content")).thenReturn(Stream.empty());
        when(messages.getAllMessages()).thenReturn(Flux.just(message));

        impersonator.addAllMessagesFromSource(messages);
        assertThatThrownBy(() -> impersonator.impersonate(user))
                .isExactlyInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User john_doe has no messages");
    }

    @Test
    void shouldImpersonateUserWithTwoTokensInOneMessage() {
        User user = mock(User.class);
        Message message = mock(Message.class);
        when(message.getAuthor()).thenReturn(user);
        when(message.getText()).thenReturn("content");
        when(stringTokenizer.tokenize("content")).thenReturn(Stream.of("0", "1"));
        when(messages.getAllMessages()).thenReturn(Flux.just(message));
        when(random.nextInt(2)).thenReturn(1, 0, 0, 1, 1);

        impersonator.addAllMessagesFromSource(messages);
        String impersonation = impersonator.impersonate(user);

        assertThat(impersonation).isEqualTo("1. 0. 0. 1. 1");
    }

    @Test
    void shouldImpersonateWithThreeTokensInTwoMessages() {
        User user = mock(User.class);
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        when(message1.getAuthor()).thenReturn(user);
        when(message1.getText()).thenReturn("content1");
        when(stringTokenizer.tokenize("content1")).thenReturn(Stream.of("0", "1"));
        when(message2.getAuthor()).thenReturn(user);
        when(message2.getText()).thenReturn("content2");
        when(stringTokenizer.tokenize("content2")).thenReturn(Stream.of("2"));
        when(messages.getAllMessages()).thenReturn(Flux.just(message1, message2));
        when(random.nextInt(3)).thenReturn(1, 2, 0, 1, 2);

        impersonator.addAllMessagesFromSource(messages);
        String impersonation = impersonator.impersonate(user);

        assertThat(impersonation).isEqualTo("1. 2. 0. 1. 2");
    }

    @Test
    void shouldImpersonateAfterAddingMessageToModel() {
        User user = mock(User.class);
        Message message = mock(Message.class);
        when(message.getAuthor()).thenReturn(user);
        when(message.getText()).thenReturn("content");
        when(stringTokenizer.tokenize("content")).thenReturn(Stream.of("0", "1"));
        when(random.nextInt(2)).thenReturn(1, 0, 0, 1, 1);

        impersonator.addMessage(message);
        String impersonation = impersonator.impersonate(user);

        assertThat(impersonation).isEqualTo("1. 0. 0. 1. 1");
    }
}