package dappercloud.cocobot.application;

import dappercloud.cocobot.discord.ExcludeCommandsDiscordMessagesFilter;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;
import dappercloud.cocobot.domain.MessagesFilterImpersonatorDecorator;
import dappercloud.cocobot.domain.MessagesSource;
import dappercloud.cocobot.domain.SentencesTokenizer;
import dappercloud.cocobot.domain.SimpleTokensRandomImpersonator;

import java.util.Optional;
import java.util.Random;

public class ImpersonationTestingChatBotApplication implements ChatBot{

    private final Impersonator simpleSentencesImpersonator;
    public ImpersonationTestingChatBotApplication(MessagesSource source) {
        simpleSentencesImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new SimpleTokensRandomImpersonator(new SentencesTokenizer(), new Random())
        );
        simpleSentencesImpersonator.addAllMessagesFromSource(source);
    }

    @Override
    public Optional<MessageReply> handleMessage(Message message) {
        if (message.getText().startsWith("c/sentences")) {
            return Optional.of(new MessageReply(simpleSentencesImpersonator.impersonate(message.getAuthor())));
        }
        return Optional.empty();
    }
}
