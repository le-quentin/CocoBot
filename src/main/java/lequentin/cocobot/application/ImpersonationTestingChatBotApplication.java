package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.LongImpersonationImpersonatorDecorator;
import lequentin.cocobot.domain.MarkovImpersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessageReply;
import lequentin.cocobot.domain.MessagesFilterImpersonatorDecorator;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.domain.MultipleSentencesImpersonatorDecorator;
import lequentin.cocobot.domain.SentencesStringTokenizer;
import lequentin.cocobot.domain.SimpleTokensRandomImpersonator;
import lequentin.cocobot.domain.StringTokenizer;
import lequentin.cocobot.domain.WordsStringTokenizer;
import lequentin.cocobot.domain.markov.SimpleMarkovChainsWalker;
import lequentin.cocobot.domain.markov.MarkovTokenizer;

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
                new ExcludeChatCommandsMessagesFilter(),
                new SimpleTokensRandomImpersonator(sentencesStringTokenizer, new Random())
        );
        simpleSentencesImpersonator.addAllMessagesFromSource(source);

        WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        MarkovTokenizer markov2Tokenizer = new MarkovTokenizer(wordsTokenizer, 2);
        Impersonator markov2Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov2Tokenizer, new SimpleMarkovChainsWalker<>(new Random()))
        );
        markov2Impersonator = new LongImpersonationImpersonatorDecorator(markov2Impersonator, 30, 200);
        markov2Impersonator.addAllMessagesFromSource(source);
        this.markov2Impersonator = markov2Impersonator;

        MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(wordsTokenizer, 3);
        Impersonator markov3Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, new SimpleMarkovChainsWalker<>(new Random()))
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
