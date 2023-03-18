package lequentin.cocobot.domain;


import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public interface Impersonator {
    default Flux<Message> addAllMessagesFromSource(MessagesSource messagesSource) {
        return this.addAllMessagesFromSource(messagesSource, () -> {});
    }

    default Flux<Message> addAllMessagesFromSource(MessagesSource messagesSource, Runnable onComplete) {
        return messagesSource.getAllMessages()
                 .subscribeOn(Schedulers.elastic())
                .map(message -> {
                    this.addMessage(message);
                    return message;
                })
                .doOnComplete(onComplete);
    }

    void addMessage(Message message);

    String impersonate(User user);
}
