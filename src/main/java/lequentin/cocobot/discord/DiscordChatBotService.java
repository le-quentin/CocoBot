package lequentin.cocobot.discord;

import lequentin.cocobot.application.ChatBot;
import lequentin.cocobot.domain.MessageReply;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

import java.util.Optional;

public class DiscordChatBotService {

    private final DiscordConverter converter;
    private final ChatBot coco;
    private final MessageClient client;

    public DiscordChatBotService(DiscordConverter converter, ChatBot coco, MessageClient client) {
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
