package dappercloud.cocobot.discord;

import dappercloud.cocobot.domain.CocoBot;
import dappercloud.cocobot.domain.MessageReply;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

import java.util.Optional;

public class CocoFluxService {

    private final DiscordConverter converter;
    private final CocoBot coco;
    private final MessageClient client;

    public CocoFluxService(DiscordConverter converter, CocoBot coco, MessageClient client) {
        this.converter = converter;
        this.coco = coco;
        this.client = client;
    }

    public void subscribeToMessageCreateFlux(Flux<MessageCreateEvent> eventFlux) {
        eventFlux.subscribe(event -> {
            final Message message = event.getMessage();
            try {
                Optional<MessageReply> messageReply = coco.handleMessage(converter.toDomain(message));
                messageReply.ifPresent(reply -> client.replyToMessage(message, reply.getText()));
            } catch(Exception ex) {
                System.err.println("Exception while handling message: " + message.getContent());
                ex.printStackTrace(System.err);
            }
        });
    }
}
