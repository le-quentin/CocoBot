package dappercloud.cocobot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocoApplicationUnitTest {

    @Mock
    private DiscordClient discordClient;

    @Mock
    private CocoFluxService service;

    @InjectMocks
    private CocoApplication app;

    @SuppressWarnings("unchecked")
    @Test
    void shouldRun() {
        Mono<GatewayDiscordClient> monoLogin = (Mono<GatewayDiscordClient>)mock(Mono.class);
        Mono<Void> monoDisconnect = (Mono<Void>)mock(Mono.class);
        GatewayDiscordClient gateway = mock(GatewayDiscordClient.class);
        Flux<MessageCreateEvent> eventFlux = (Flux<MessageCreateEvent>)mock(Flux.class);
        when(discordClient.login()).thenReturn(monoLogin);
        when(monoLogin.block()).thenReturn(gateway);
        when(gateway.on(MessageCreateEvent.class)).thenReturn(eventFlux);
        when(gateway.onDisconnect()).thenReturn(monoDisconnect);

        app.run();

        ArgumentCaptor<Consumer<MessageCreateEvent>> eventConsumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(monoLogin).block();
        verify(service).subscribeToMessageCreateFlux(eventFlux);
        verify(monoDisconnect).block();
    }

}