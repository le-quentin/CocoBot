package dappercloud.cocobot.application;

import dappercloud.cocobot.discord.ExcludeCommandsDiscordMessagesFilter;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.LongImpersonationImpersonatorDecorator;
import dappercloud.cocobot.domain.MarkovImpersonator;
import dappercloud.cocobot.domain.Message;
import dappercloud.cocobot.domain.MessageReply;
import dappercloud.cocobot.domain.MessagesFilterImpersonatorDecorator;
import dappercloud.cocobot.domain.MessagesSource;
import dappercloud.cocobot.domain.MultipleSentencesImpersonatorDecorator;
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
    private final Impersonator multi2Impersonator;
    private final Impersonator multi4Impersonator;

    public ImpersonationTestingChatBotApplication(MessagesSource source) {
        StringTokenizer sentencesStringTokenizer = new SentencesStringTokenizer();
        simpleSentencesImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new SimpleTokensRandomImpersonator(sentencesStringTokenizer, new Random())
        );
        simpleSentencesImpersonator.addAllMessagesFromSource(source);

        WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        MarkovTokenizer markov2Tokenizer = new MarkovTokenizer(wordsTokenizer, 2);
        Impersonator markov2Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov2Tokenizer, new MarkovChains<>(), new Random())
        );
        markov2Impersonator = new LongImpersonationImpersonatorDecorator(markov2Impersonator, 30, 200);
        markov2Impersonator.addAllMessagesFromSource(source);
        this.markov2Impersonator = markov2Impersonator;

        MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(wordsTokenizer, 3);
        Impersonator markov3Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, new MarkovChains<>(), new Random())
        );
        markov3Impersonator.addAllMessagesFromSource(source);
        this.markov3Impersonator = new LongImpersonationImpersonatorDecorator(markov3Impersonator, 30, 200);

        this.multi2Impersonator = new MultipleSentencesImpersonatorDecorator(
                new LongImpersonationImpersonatorDecorator(markov3Impersonator, 30, 200),
                2
        );

        this.multi4Impersonator = new MultipleSentencesImpersonatorDecorator(
                new LongImpersonationImpersonatorDecorator(markov3Impersonator, 15, 200),
                4
        );
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
        if (message.getText().startsWith("c/multi2")) {
            return Optional.of(new MessageReply(multi2Impersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/multi4")) {
            return Optional.of(new MessageReply(multi4Impersonator.impersonate(message.getAuthor())));
        }

        return Optional.empty();
    }
}
