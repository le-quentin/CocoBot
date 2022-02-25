package dappercloud.cocobot.domain;

import reactor.core.publisher.Flux;

@FunctionalInterface
public interface MessagesSource {
    Flux<Message> getAllMessages();
}
