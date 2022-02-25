package dappercloud.cocobot.application;

import dappercloud.cocobot.discord.ExcludeCommandsDiscordMessagesFilter;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MarkovImpersonator;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;
import dappercloud.cocobot.domain.MessagesFilterImpersonatorDecorator;
import dappercloud.cocobot.domain.MessagesSource;
import dappercloud.cocobot.domain.SentencesTokenizer;
import dappercloud.cocobot.domain.SimpleTokensRandomImpersonator;
import dappercloud.cocobot.domain.Tokenizer;
import dappercloud.cocobot.domain.WordsTokenizer;
import dappercloud.cocobot.domain.markov.MarkovChains;
import dappercloud.cocobot.domain.markov.MarkovTokenizer;

import java.util.Optional;
import java.util.Random;

public class ImpersonationTestingChatBotApplication implements ChatBot{

    private final Impersonator simpleSentencesImpersonator;
    private final Impersonator markovImpersonator;

    public ImpersonationTestingChatBotApplication(MessagesSource source) {
        Tokenizer sentencesTokenizer = new SentencesTokenizer();
        simpleSentencesImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new SimpleTokensRandomImpersonator(sentencesTokenizer, new Random())
        );
        simpleSentencesImpersonator.addAllMessagesFromSource(source);

        WordsTokenizer wordsTokenizer = new WordsTokenizer();
        Tokenizer markovTokenizer = new MarkovTokenizer(wordsTokenizer, 3);
        markovImpersonator = new MarkovImpersonator(sentencesTokenizer, markovTokenizer, new MarkovChains<>());
        markovImpersonator.addAllMessagesFromSource(source);
    }

    @Override
    public Optional<MessageReply> handleMessage(Message message) {
        if (message.getText().startsWith("c/sentences")) {
            return Optional.of(new MessageReply(simpleSentencesImpersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/markov")) {
            return Optional.of(new MessageReply(markovImpersonator.impersonate(message.getAuthor())));
        }
        return Optional.empty();
    }
}
