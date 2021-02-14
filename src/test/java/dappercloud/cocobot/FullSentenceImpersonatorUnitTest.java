package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FullSentenceImpersonatorUnitTest {

    @Mock
    private MessagesRepository messages;

    @Mock
    private Random random;

    @InjectMocks
    private FullSentenceImpersonator impersonator;

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
    void shouldImpersonateUserWithTwoSentencesInOneMessage() {
        User user = mock(User.class);
        Message message = mock(Message.class);
        when(message.getAuthor()).thenReturn(Optional.of(user));
        when(message.getContent()).thenReturn("First sentence. Second sentence");
        when(messages.getAllMessages()).thenReturn(Flux.just(message));
        when(random.nextInt(2)).thenReturn(1, 0, 0, 1, 1);

        impersonator.buildModel(messages);
        String impersonation = impersonator.impersonate(user);

        assertThat(impersonation).isEqualTo("Second sentence. First sentence. First sentence. Second sentence. Second sentence");
    }

    @Test
    void shouldImpersonateWithThreeSentencesInTwoMessages() {
        User user = mock(User.class);
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        when(message1.getAuthor()).thenReturn(Optional.of(user));
        when(message1.getContent()).thenReturn("First sentence. Second sentence");
        when(message2.getAuthor()).thenReturn(Optional.of(user));
        when(message2.getContent()).thenReturn("Third sentence   ");
        when(messages.getAllMessages()).thenReturn(Flux.just(message1, message2));
        when(random.nextInt(3)).thenReturn(1, 2, 0, 1, 2);

        impersonator.buildModel(messages);
        String impersonation = impersonator.impersonate(user);

        assertThat(impersonation).isEqualTo("Second sentence. Third sentence. First sentence. Second sentence. Third sentence");
    }

    @Test
    void shouldNotImpersonateWhenUserHasOnlyEmptyMessages() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("john_doe");
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        when(message1.getAuthor()).thenReturn(Optional.of(user));
        when(message1.getContent()).thenReturn("    . !!!!. . ? ?");
        when(message2.getAuthor()).thenReturn(Optional.of(user));
        when(message2.getContent()).thenReturn("!!!!????.. . ? ?");
        when(messages.getAllMessages()).thenReturn(Flux.just(message1, message2));

        impersonator.buildModel(messages);

        assertThatThrownBy(() -> impersonator.impersonate(user))
                .isExactlyInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User john_doe has no messages");
    }

    @Test
    void shouldImpersonateAfterAddingMessageToModel() {
        User user = mock(User.class);
        Message message = mock(Message.class);
        when(message.getAuthor()).thenReturn(Optional.of(user));
        when(message.getContent()).thenReturn("First sentence. Second sentence");
        when(random.nextInt(2)).thenReturn(1, 0, 0, 1, 1);

        impersonator.addToModel(message);
        String impersonation = impersonator.impersonate(user);

        assertThat(impersonation).isEqualTo("Second sentence. First sentence. First sentence. Second sentence. Second sentence");
    }
}