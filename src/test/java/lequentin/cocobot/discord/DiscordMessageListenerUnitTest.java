package lequentin.cocobot.discord;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lequentin.cocobot.application.CocoChatBotApplication;
import lequentin.cocobot.application.IncomingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscordMessageListenerUnitTest {

    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @Captor
    private ArgumentCaptor<IncomingMessage> incomingMessageArgumentCaptor;

    @Mock
    private DiscordConverter converter;

    @Mock
    private CocoChatBotApplication coco;

    @InjectMocks
    private DiscordMessageListener service;

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(errorStreamCaptor));

    }

    @Test
    void shouldSubscribeToFluxAndHandleMessages() {
        Consumer<MessageCreateEvent> subscribedConsumer = getSubscribedConsumer();
        MessageCreateEvent testEvent = mock(MessageCreateEvent.class);
        discord4j.core.object.entity.Message discordMessage = mock(discord4j.core.object.entity.Message.class);
        when(testEvent.getMessage()).thenReturn(discordMessage);

        subscribedConsumer.accept(testEvent);

        verify(coco).handleMessage(incomingMessageArgumentCaptor.capture());
        IncomingMessage handledMessage = incomingMessageArgumentCaptor.getValue();
        assertThat(handledMessage)
                .usingRecursiveComparison()
                .isEqualTo(new DiscordIncomingMessage(discordMessage, converter));
    }

    @SuppressWarnings("unchecked")
    private Consumer<MessageCreateEvent> getSubscribedConsumer() {
        Flux<MessageCreateEvent> eventFlux = (Flux<MessageCreateEvent>) mock(Flux.class);
        service.subscribeToMessageCreateFlux(eventFlux);
        ArgumentCaptor<Consumer<MessageCreateEvent>> eventConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(eventFlux).subscribe(eventConsumerCaptor.capture());
        return eventConsumerCaptor.getValue();
    }

    @Test
    void shouldHandleExceptionAndKeepRunning() throws NoSuchFieldException {
        MessageCreateEvent event1 = mock(MessageCreateEvent.class);
        MessageCreateEvent event2 = mock(MessageCreateEvent.class);
        discord4j.core.object.entity.Message discordMessage1 = mock(discord4j.core.object.entity.Message.class);
        when(discordMessage1.getContent()).thenReturn("erroneous message");
        discord4j.core.object.entity.Message discordMessage2 = mock(discord4j.core.object.entity.Message.class);
        when(event1.getMessage()).thenReturn(discordMessage1);
        when(event2.getMessage()).thenReturn(discordMessage2);
        RuntimeException thrown = mock(RuntimeException.class);
        doThrow(thrown).when(coco).handleMessage(argThat(matchDiscordMessage(discordMessage1)));
        Flux<MessageCreateEvent> fluxWithException = Flux.just(event1, event2);

        service.subscribeToMessageCreateFlux(fluxWithException);

        verify(coco).handleMessage(argThat(matchDiscordMessage(discordMessage1)));
        assertThat(errorStreamCaptor.toString()).contains("Exception while handling message: erroneous message");
        verify(thrown).printStackTrace(System.err);
        verify(coco).handleMessage(argThat(matchDiscordMessage(discordMessage2)));
    }

    private ArgumentMatcher<DiscordIncomingMessage> matchDiscordMessage(discord4j.core.object.entity.Message discordMessage) throws NoSuchFieldException {
        Field field = DiscordIncomingMessage.class.getDeclaredField("discordMessage");
        field.setAccessible(true);
        return m -> {
            try {
                return field.get(m) == discordMessage;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}