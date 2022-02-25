package dappercloud.cocobot;

import dappercloud.cocobot.discord.DiscordChatBotService;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CocoApplicationUnitTest {

    @Mock
    private GatewayDiscordClient gatewayDiscordClient;

    @Mock
    private DiscordChatBotService service;

    @InjectMocks
    private CocoApplication app;

    @SuppressWarnings("unchecked")
    @Test
    void shouldRun() {
        Mono<Void> monoDisconnect = (Mono<Void>)mock(Mono.class);
        Flux<MessageCreateEvent> eventFlux = (Flux<MessageCreateEvent>)mock(Flux.class);
        when(gatewayDiscordClient.on(MessageCreateEvent.class)).thenReturn(eventFlux);
        when(gatewayDiscordClient.onDisconnect()).thenReturn(monoDisconnect);

        app.run();

        verify(service).subscribeToMessageCreateFlux(eventFlux);
        verify(monoDisconnect).block();
    }

}