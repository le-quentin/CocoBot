package lequentin.cocobot.domain;

import reactor.core.publisher.Flux;

public interface MessagesRepository extends MessagesSource {
    Flux<Message> getAllMessages();
    void synchronise(MessagesSource externalSource);
}
