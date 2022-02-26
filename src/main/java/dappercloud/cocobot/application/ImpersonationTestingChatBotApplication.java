package dappercloud.cocobot.application;

import dappercloud.cocobot.discord.ExcludeCommandsDiscordMessagesFilter;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MarkovImpersonator;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;
import dappercloud.cocobot.domain.MessagesFilterImpersonatorDecorator;
import dappercloud.cocobot.domain.MessagesSource;
import dappercloud.cocobot.domain.SentencesStringTokenizer;
import dappercloud.cocobot.domain.SimpleTokensRandomImpersonator;
import dappercloud.cocobot.domain.StringTokenizer;
import dappercloud.cocobot.domain.WordsStringTokenizer;
import dappercloud.cocobot.domain.markov.MarkovChains;
import dappercloud.cocobot.domain.markov.MarkovTokenizer;

import java.util.Optional;
import java.util.Random;

public class ImpersonationTestingChatBotApplication implements ChatBot{

    private final Impersonator simpleSentencesImpersonator;
    private final Impersonator markov3Impersonator;
    private final Impersonator markov2Impersonator;

    public ImpersonationTestingChatBotApplication(MessagesSource source) {
        StringTokenizer sentencesStringTokenizer = new SentencesStringTokenizer();
        simpleSentencesImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new SimpleTokensRandomImpersonator(sentencesStringTokenizer, new Random())
        );
        simpleSentencesImpersonator.addAllMessagesFromSource(source);

        WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        MarkovTokenizer markov2Tokenizer = new MarkovTokenizer(wordsTokenizer, 2);
        markov2Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov2Tokenizer, new MarkovChains<>(), new Random())
        );
        markov2Impersonator.addAllMessagesFromSource(source);

        MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(wordsTokenizer, 3);
        markov3Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, new MarkovChains<>(), new Random())
        );
        markov3Impersonator.addAllMessagesFromSource(source);
    }

    @Override
    public Optional<MessageReply> handleMessage(Message message) {
        if (message.getText().startsWith("c/sentences")) {
            return Optional.of(new MessageReply(simpleSentencesImpersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/markov2")) {
            return Optional.of(new MessageReply(markov2Impersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/markov3")) {
            return Optional.of(new MessageReply(markov3Impersonator.impersonate(message.getAuthor())));
        }

        return Optional.empty();
    }
}
