package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.domain.StringTokenizer;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.impersonator.LongImpersonationImpersonatorDecorator;
import lequentin.cocobot.domain.impersonator.MarkovImpersonator;
import lequentin.cocobot.domain.impersonator.MessagesFilterImpersonatorDecorator;
import lequentin.cocobot.domain.impersonator.MultipleSentencesImpersonatorDecorator;
import lequentin.cocobot.domain.markov.FindMaxOverBatchOfPathWalkerDecorator;
import lequentin.cocobot.domain.markov.MarkovChains;
import lequentin.cocobot.domain.markov.MarkovChainsWalker;
import lequentin.cocobot.domain.markov.MarkovTokenizer;
import lequentin.cocobot.domain.markov.MarkovWordsGenerator;
import lequentin.cocobot.domain.markov.SimpleMarkovChainsWalker;
import lequentin.cocobot.domain.markov.WordsTuple;
import lequentin.cocobot.domain.sanitizer.SpacePunctuationSanitizer;
import lequentin.cocobot.domain.tokenizer.SanitizerStringTokenizerDecorator;
import lequentin.cocobot.domain.tokenizer.SentencesStringTokenizer;
import lequentin.cocobot.domain.tokenizer.WordsStringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * This is my "prototyping" chat bot. I use it to try and compare different settings, and see what produces the funniest outputs!
 */
public class ImpersonationTestingChatBotApplication implements ChatBot {

    private static Logger log = LoggerFactory.getLogger(ImpersonationTestingChatBotApplication.class);

    private final Impersonator simpleSentencesImpersonator = null;

    private final MarkovImpersonator markov3Impersonator;
    private final MarkovImpersonator markov3PunctuationImpersonator;
    private final MarkovImpersonator markov2Impersonator = null;

    private Impersonator multi2Impersonator;
    private final Impersonator multi4Impersonator = null;

    private final Impersonator markov3BatchWalkerImpersonator;
    private final Impersonator markov3BatchWalkerPunctuationImpersonator;
    private final Impersonator markov2BatchWalkerImpersonator = null;

    public ImpersonationTestingChatBotApplication(MessagesSource source) {
        StringTokenizer sentencesStringTokenizer = new SentencesStringTokenizer();
        StringTokenizer sentencesStringPunctuationTokenizer = new SanitizerStringTokenizerDecorator(new SpacePunctuationSanitizer(), new SentencesStringTokenizer());

        WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        StringTokenizer wordsAndPunctuationTokenizer = new SanitizerStringTokenizerDecorator(new SpacePunctuationSanitizer(), wordsTokenizer);
        MarkovTokenizer markov2Tokenizer = new MarkovTokenizer(wordsTokenizer, 2);
        MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(wordsTokenizer, 3);
        MarkovTokenizer markov3PunctuationTokenizer = new MarkovTokenizer(wordsAndPunctuationTokenizer, 3);

//        Impersonator markov2Impersonator = new MessagesFilterImpersonatorDecorator(
//                new ExcludeChatCommandsMessagesFilter(),
//                new MarkovImpersonator(sentencesStringTokenizer, markov2Tokenizer, new SimpleMarkovChainsWalker<>(new Random()))
//        );
//        markov2Impersonator = new LongImpersonationImpersonatorDecorator(markov2Impersonator, 30, 200);
//        markov2Impersonator.addAllMessagesFromSource(source);
//        this.markov2Impersonator = markov2Impersonator;
//

//        Impersonator markov3Impersonator = new MessagesFilterImpersonatorDecorator(
//                new ExcludeChatCommandsMessagesFilter(),
//                new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, new SimpleMarkovChainsWalker<>(new Random()))
//        );
//        markov3Impersonator.addAllMessagesFromSource(source);
//        this.markov3Impersonator = new LongImpersonationImpersonatorDecorator(markov3Impersonator, 30, 200);

//        this.multi2Impersonator = new MultipleSentencesImpersonatorDecorator(
//                new LongImpersonationImpersonatorDecorator(markov3Impersonator, 15, 200),
//                2
//        );

//        this.multi4Impersonator = new MultipleSentencesImpersonatorDecorator(
//                new LongImpersonationImpersonatorDecorator(markov3Impersonator, 15, 200),
//                4
//        );


        final MarkovChainsWalker<WordsTuple> leastDeterministicWalker = new FindMaxOverBatchOfPathWalkerDecorator<>(
                new SimpleMarkovChainsWalker<>(new Random()),
                Comparator.comparingInt(path -> (int)Math.round(path.getNonDeterministicScore() * (Math.log10(path.getLength())))),
                50,
                0
        );

        final MarkovChainsWalker<WordsTuple> mostDeterministicWalker = new FindMaxOverBatchOfPathWalkerDecorator<>(
                new SimpleMarkovChainsWalker<>(new Random()),
                Comparator.comparingInt(path -> path.getLength()*100000/path.getNonDeterministicScore()),
                400,
                20
        );

//        this.markov2Impersonator = new MarkovImpersonator(sentencesStringTokenizer, markov2Tokenizer, mostDeterministicWalker);
//        Impersonator markov2BatchImpersonator = new MessagesFilterImpersonatorDecorator(
//                new ExcludeChatCommandsMessagesFilter(),
//                markov2Impersonator
//        );
//        markov2BatchImpersonator.addAllMessagesFromSource(source);
//        this.markov2BatchWalkerImpersonator = new LongImpersonationImpersonatorDecorator(markov2BatchImpersonator, 4, 200);
//
        this.markov3Impersonator = new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, leastDeterministicWalker);
        this.markov3PunctuationImpersonator = new MarkovImpersonator(sentencesStringTokenizer, markov3PunctuationTokenizer, leastDeterministicWalker);
        Impersonator markov3BatchImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                markov3Impersonator
        );
        Impersonator markov3BatchPunctuationImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                markov3PunctuationImpersonator
        );
        this.markov3BatchWalkerImpersonator = new LongImpersonationImpersonatorDecorator(markov3BatchImpersonator, 4, 200);
        this.markov3BatchWalkerPunctuationImpersonator = new LongImpersonationImpersonatorDecorator(markov3BatchPunctuationImpersonator, 4, 200);

//        markov3Impersonator.addAllMessagesFromSource(source);
        markov3PunctuationImpersonator.addAllMessagesFromSource(source);

        this.multi2Impersonator = new MultipleSentencesImpersonatorDecorator(
                markov3PunctuationImpersonator,
                2
        );

    }

    @Override
    public void handleMessage(IncomingMessage incomingMessage) {
        Message message = incomingMessage.toDomain();
        if (message.getText().startsWith("c/sentences")) {
            incomingMessage.reply(new BotMessage(simpleSentencesImpersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/markov2")) {
            incomingMessage.reply(new BotMessage(markov2Impersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/markov3")) {
            incomingMessage.reply(new BotMessage(markov3Impersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/multi2")) {
            incomingMessage.reply(new BotMessage(multi2Impersonator.impersonate(message.getAuthor())));
        }
        if (message.getText().startsWith("c/multi4")) {
            incomingMessage.reply(new BotMessage(multi4Impersonator.impersonate(message.getAuthor())));
        }
    }

    void sample() {
        List<User> authors = List.of(
                new User("DapperCloud"),
                new User("Hisatak"),
                new User("Nasvar"),
                new User("Zukajin"),
                new User("Monsieur Blu"),
                new User("Luo Sha")
        );

        Map<String, Impersonator> impersonators = Map.of(
//                "markov2", markov2Impersonator,
//                "markov3", markov3Impersonator,
//                "multi2", multi2Impersonator,
//                "markov2Batch", markov2BatchWalkerImpersonator,
//                "markov3Batch", markov3BatchWalkerImpersonator,
//                "markov3Batch (punctuation)", markov3BatchWalkerPunctuationImpersonator
                "multi2 (batch + punctuation)", multi2Impersonator
        );

        impersonators.forEach((name, impersonator) -> {
            log.info(name + "\n------------------------------");
            authors.forEach(user -> {
                log.info(user.getUsername());
                IntStream.range(1, 6).forEach(i -> log.info(i + ": " + impersonator.impersonate(user)));
            });
        });
    }

    void printChainsMetadata() throws Exception {
        Map<String, MarkovImpersonator> markovs = Map.of(
//                "markov2", markov2Impersonator,
//                "markov3", markov3Impersonator,
//                "multi2", multi2Impersonator,
//                "markov2", markov2Impersonator,
//                "markov3", markov3Impersonator
                "markov3 (punctuation)", markov3PunctuationImpersonator
        );

        List<User> authors = List.of(
                new User("DapperCloud"),
                new User("Hisatak"),
                new User("Nasvar"),
                new User("Zukajin"),
                new User("Monsieur Blu"),
                new User("Luo Sha")
        );
        for (Entry<String, MarkovImpersonator> entry : markovs.entrySet()) {
            String name = entry.getKey();
            MarkovImpersonator markov = entry.getValue();
            Map<User, MarkovWordsGenerator> userMarkovGenerators = (Map<User, MarkovWordsGenerator>) getPrivateField(markov
                    .getClass()
                    .getDeclaredField("userMarkovGenerators"))
                    .get(markov);
            log.info(name + "\n------------------------------");
            for (User user : authors) {
                MarkovChains<WordsTuple> chains = (MarkovChains<WordsTuple>) getPrivateField(userMarkovGenerators.get(user).getClass()
                        .getDeclaredField("markovChains"))
                        .get(userMarkovGenerators.get(user));
                log.info(user.getUsername() + ": " + chains.getMetadata());
            }
        }
    }

    private Field getPrivateField(Field field) {
        field.setAccessible(true);
        return field;
    }
}
