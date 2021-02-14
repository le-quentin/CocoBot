package dappercloud.cocobot;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public interface Impersonator {
    void buildModel(MessagesRepository messagesRepository);
    void addToModel(Message message);
    String impersonate(User user);
}
