package lequentin.cocobot.discord;

import lequentin.cocobot.application.CocoChatBotApplication;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessageReply;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscordChatBotServiceUnitTest {

    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private DiscordConverter converter;

    @Mock
    private CocoChatBotApplication coco;

    @Mock
    private MessageClient client;

    @InjectMocks
    private DiscordChatBotService service;

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(errorStreamCaptor));

    }

    @Test
    void shouldSubscribeToFluxAndHandleMessages() {
        Consumer<MessageCreateEvent> subscribedConsumer = getSubscribedConsumer();
        MessageCreateEvent testEvent = mock(MessageCreateEvent.class);
        discord4j.core.object.entity.Message discordMessage = mock(discord4j.core.object.entity.Message.class);
        Message message = mock(Message.class);
        when(testEvent.getMessage()).thenReturn(discordMessage);
        when(converter.toDomain(discordMessage)).thenReturn(message);
        MessageReply reply = mock(MessageReply.class);
        when(reply.getText()).thenReturn("the reply");
        when(coco.handleMessage(message)).thenReturn(Optional.of(reply));

        subscribedConsumer.accept(testEvent);

        verify(client).replyToMessage(discordMessage, "the reply");
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
    void shouldHandleExceptionAndKeepRunning() {
        MessageCreateEvent event1 = mock(MessageCreateEvent.class);
        MessageCreateEvent event2 = mock(MessageCreateEvent.class);
        discord4j.core.object.entity.Message discordMessage1 = mock(discord4j.core.object.entity.Message.class);
        when(discordMessage1.getContent()).thenReturn("erroneous message");
        discord4j.core.object.entity.Message discordMessage2 = mock(discord4j.core.object.entity.Message.class);
        when(event1.getMessage()).thenReturn(discordMessage1);
        when(event2.getMessage()).thenReturn(discordMessage2);
        Message message1 = mock(Message.class);
        Message message2 = mock(Message.class);
        when(converter.toDomain(discordMessage1)).thenReturn(message1);
        when(converter.toDomain(discordMessage2)).thenReturn(message2);
        RuntimeException thrown = mock(RuntimeException.class);
        doThrow(thrown).when(coco).handleMessage(message1);
        Flux<MessageCreateEvent> fluxWithException = Flux.just(event1, event2);

        service.subscribeToMessageCreateFlux(fluxWithException);

        verify(coco).handleMessage(message1);
        assertThat(errorStreamCaptor.toString()).contains("Exception while handling message: erroneous message");
        verify(thrown).printStackTrace(System.err);
        verify(coco).handleMessage(message2);
    }
}