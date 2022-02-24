package dappercloud.cocobot.domain;

import reactor.core.publisher.Flux;

public interface MessagesSource {
    Flux<Message> getAllMessages();
}
