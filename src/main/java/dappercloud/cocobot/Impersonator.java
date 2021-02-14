package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public interface Impersonator {
    void addAllMessagesFromSource(MessagesSource messagesSource);
    void addMessage(Message message);
    String impersonate(User user);
}
