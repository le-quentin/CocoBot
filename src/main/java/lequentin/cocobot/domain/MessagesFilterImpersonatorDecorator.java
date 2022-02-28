package lequentin.cocobot.domain;

public class MessagesFilterImpersonatorDecorator implements Impersonator{
    private final MessagesFilter filter;
    private final Impersonator impersonator;

    public MessagesFilterImpersonatorDecorator(MessagesFilter filter, Impersonator impersonator) {
        this.filter = filter;
        this.impersonator = impersonator;
    }

    @Override
    public void addAllMessagesFromSource(MessagesSource messagesSource) {
        MessagesSource filteredSource = () -> messagesSource.getAllMessages().filter(filter::accepts);
        impersonator.addAllMessagesFromSource(filteredSource);
    }

    @Override
    public void addMessage(Message message) {
        if (filter.accepts(message)) impersonator.addMessage(message);
    }

    @Override
    public String impersonate(User user) {
        return impersonator.impersonate(user);
    }
}
