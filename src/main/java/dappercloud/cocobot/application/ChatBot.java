package dappercloud.cocobot.application;

import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;

import java.util.Optional;

public interface ChatBot {
    Optional<MessageReply> handleMessage(Message message);
}
