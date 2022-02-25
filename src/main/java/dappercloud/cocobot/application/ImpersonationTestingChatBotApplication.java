package dappercloud.cocobot.application;

import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;

import java.util.Optional;

public class ImpersonationTestingChatBotApplication implements ChatBot{
    @Override
    public Optional<MessageReply> handleMessage(Message message) {
        System.out.println("No impersonation testing chat bot implementation yet!");
        return Optional.empty();
    }
}
