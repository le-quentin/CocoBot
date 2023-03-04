package lequentin.cocobot.domain.impersonator;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessagesFilter;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.domain.User;

public class MessagesFilterImpersonatorDecorator implements Impersonator {
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
