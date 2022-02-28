package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessageReply;

import java.util.Optional;

public interface ChatBot {
    Optional<MessageReply> handleMessage(Message message);
}
