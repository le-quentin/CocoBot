package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

public interface MessagesSource {
    Flux<Message> getAllMessages();
}
