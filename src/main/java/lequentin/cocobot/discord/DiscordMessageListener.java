package lequentin.cocobot.discord;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lequentin.cocobot.application.ChatBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

public class DiscordMessageListener {

    private static Logger log = LoggerFactory.getLogger(DiscordMessageListener.class);

    private final DiscordConverter converter;
    private final ChatBot coco;

    public DiscordMessageListener(DiscordConverter converter, ChatBot coco) {
        this.converter = converter;
        this.coco = coco;
    }

    public void subscribeToMessageCreateFlux(Flux<MessageCreateEvent> eventFlux) {
        eventFlux.subscribe(event -> {
            final Message message = event.getMessage();
            try {
                DiscordIncomingMessage incomingMessage = new DiscordIncomingMessage(message, converter);
                coco.handleMessage(incomingMessage);
            } catch(Exception ex) {
                log.error("Exception while handling message: {}", message.getContent());
                ex.printStackTrace(System.err);
            }
        });
    }
}
