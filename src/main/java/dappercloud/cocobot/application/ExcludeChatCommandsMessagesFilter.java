package dappercloud.cocobot.application;

import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessagesFilter;

public class ExcludeChatCommandsMessagesFilter implements MessagesFilter {
    @Override
    public boolean accepts(Message msg) {
        String text = msg.getText();
        if (text.length() < 3) return false;

        return !text.matches("^.?[/!].*$");
    }
}
