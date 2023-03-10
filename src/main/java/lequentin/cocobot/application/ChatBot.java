package lequentin.cocobot.application;

import lequentin.cocobot.domain.Message;

import java.util.Optional;

public interface ChatBot {
    Optional<BotMessage> handleMessage(Message message);
}
