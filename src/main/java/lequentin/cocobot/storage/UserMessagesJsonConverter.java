package lequentin.cocobot.storage;

import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;

public class UserMessagesJsonConverter {
    public Message toDomainMessage(UserMessagesJson userMessagesJson, MessageJson messageJson) {
        return new Message(new User(userMessagesJson.getUsername()), messageJson.getText());
    }

    public MessageJson toJsonMessage(Message message) {
        return new MessageJson(message.getText());
    }
}
