package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;

public interface IncomingMessage {
    Message toDomain();
    void reply(BotMessage message);
}
