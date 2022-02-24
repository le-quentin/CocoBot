package dappercloud.cocobot.domain;


public interface Impersonator {
    void addAllMessagesFromSource(MessagesSource messagesSource);
    void addMessage(Message message);
    String impersonate(User user);
}
