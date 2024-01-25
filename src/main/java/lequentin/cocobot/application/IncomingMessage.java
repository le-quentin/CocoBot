package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;
import reactor.core.publisher.Mono;

public interface IncomingMessage {
    Message toDomain();
    Mono<Message> reply(BotMessage message);
}
