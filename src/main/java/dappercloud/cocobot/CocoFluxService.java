package dappercloud.cocobot;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

public class CocoFluxService {

    private final CocoBot coco;

    public CocoFluxService(CocoBot coco) {
        this.coco = coco;
    }

    public void subscribeToMessageCreateFlux(Flux<MessageCreateEvent> eventFlux) {
        eventFlux.subscribe(event -> {
            final Message message = event.getMessage();
            try {
                coco.handleMessage(message);
            } catch(Exception ex) {
                System.err.println("Exception while handling message: " + message.getContent());
                ex.printStackTrace(System.err);
            }
        });
    }
}
