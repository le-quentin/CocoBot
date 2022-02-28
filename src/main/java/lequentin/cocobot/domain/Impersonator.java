package lequentin.cocobot.domain;


public interface Impersonator {
    default void addAllMessagesFromSource(MessagesSource messagesSource) {
        messagesSource.getAllMessages().subscribe(this::addMessage);
    }
    void addMessage(Message message);
    String impersonate(User user);
}
