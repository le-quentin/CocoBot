package dappercloud.cocobot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
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
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocoFluxServiceUnitTest {

    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private CocoBot coco;

    @InjectMocks
    private CocoFluxService service;

    @BeforeEach
    public void setUp() {
        System.setErr(new PrintStream(errorStreamCaptor));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldSubscribeToFluxAndHandleMessages() {
        Flux<MessageCreateEvent> eventFlux = (Flux<MessageCreateEvent>) mock(Flux.class);

        service.subscribeToMessageCreateFlux(eventFlux);

        ArgumentCaptor<Consumer<MessageCreateEvent>> eventConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(eventFlux).subscribe(eventConsumerCaptor.capture());
        Consumer<MessageCreateEvent> eventConsumer = eventConsumerCaptor.getValue();
        MessageCreateEvent testEvent = mock(MessageCreateEvent.class);
        Message testMessage = mock(Message.class);
        when(testEvent.getMessage()).thenReturn(testMessage);
        eventConsumer.accept(testEvent);
        verify(coco).handleMessage(testMessage);
    }

    @Test
    void shouldHandleExceptionAndKeepRunning() {
        MessageCreateEvent event1 = mock(MessageCreateEvent.class);
        MessageCreateEvent event2 = mock(MessageCreateEvent.class);
        Message message1 = mock(Message.class);
        when(message1.getContent()).thenReturn("erroneous message");
        Message message2 = mock(Message.class);
        when(event1.getMessage()).thenReturn(message1);
        when(event2.getMessage()).thenReturn(message2);
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