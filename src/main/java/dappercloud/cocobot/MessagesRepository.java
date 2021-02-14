package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Flux;

public interface MessagesRepository {
    Flux<Message> getAllMessages();
}
