package lequentin.cocobot.application;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.LongImpersonationImpersonatorDecorator;
import lequentin.cocobot.domain.MarkovImpersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessageReply;
import lequentin.cocobot.domain.MessagesFilterImpersonatorDecorator;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.domain.SentencesStringTokenizer;
import lequentin.cocobot.domain.SimpleTokensRandomImpersonator;
import lequentin.cocobot.domain.StringTokenizer;
import lequentin.cocobot.domain.User;
import lequentin.cocobot.domain.WordsStringTokenizer;
import lequentin.cocobot.domain.markov.FindMaxOverBatchOfPathWalkerDecorator;
import lequentin.cocobot.domain.markov.MarkovChains;
import lequentin.cocobot.domain.markov.MarkovChainsWalker;
import lequentin.cocobot.domain.markov.MarkovTokenizer;
import lequentin.cocobot.domain.markov.MarkovWordsGenerator;
import lequentin.cocobot.domain.markov.SimpleMarkovChainsWalker;
import lequentin.cocobot.domain.markov.WordsTuple;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

public class ImpersonationTestingChatBotApplication implements ChatBot{

    private final Impersonator simpleSentencesImpersonator;

    private final MarkovImpersonator markov3Impersonator;
    private final MarkovImpersonator markov2Impersonator;

    private final Impersonator multi2Impersonator = null;
    private final Impersonator multi4Impersonator = null;

    private final Impersonator markov3BatchWalkerImpersonator;
    private final Impersonator markov2BatchWalkerImpersonator;

    public ImpersonationTestingChatBotApplication(MessagesSource source) {
        StringTokenizer sentencesStringTokenizer = new SentencesStringTokenizer();
        simpleSentencesImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                new SimpleTokensRandomImpersonator(sentencesStringTokenizer, new Random())
        );
        simpleSentencesImpersonator.addAllMessagesFromSource(source);

        WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        MarkovTokenizer markov2Tokenizer = new MarkovTokenizer(wordsTokenizer, 2);
        MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(wordsTokenizer, 3);

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

        this.markov2Impersonator = new MarkovImpersonator(sentencesStringTokenizer, markov2Tokenizer, mostDeterministicWalker);
        Impersonator markov2BatchImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                markov2Impersonator
        );
        markov2BatchImpersonator.addAllMessagesFromSource(source);
        this.markov2BatchWalkerImpersonator = new LongImpersonationImpersonatorDecorator(markov2BatchImpersonator, 4, 200);

        this.markov3Impersonator = new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, leastDeterministicWalker);
        Impersonator markov3BatchImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                markov3Impersonator
        );
        markov3BatchImpersonator.addAllMessagesFromSource(source);
        this.markov3BatchWalkerImpersonator = new LongImpersonationImpersonatorDecorator(markov3BatchImpersonator, 4, 200);
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
                "markov3Batch", markov3BatchWalkerImpersonator
        );

        impersonators.forEach((name, impersonator) -> {
            System.out.println(name + "\n------------------------------");
            authors.forEach(user -> {
                System.out.println(user.getUsername());
                IntStream.range(1, 6).forEach(i -> System.out.println(i + ": " + impersonator.impersonate(user)));
            });
        });
    }

    void printChainsMetadata() throws Exception {
        Map<String, MarkovImpersonator> markovs = Map.of(
//                "markov2", markov2Impersonator,
//                "markov3", markov3Impersonator,
//                "multi2", multi2Impersonator,
                "markov2", markov2Impersonator,
                "markov3", markov3Impersonator
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
            System.out.println(name + "\n------------------------------");
            for (User user : authors) {
                MarkovChains<WordsTuple> chains = (MarkovChains<WordsTuple>) getPrivateField(userMarkovGenerators.get(user).getClass()
                        .getDeclaredField("markovChains"))
                        .get(userMarkovGenerators.get(user));
                System.out.println(user.getUsername() + ": " + chains.getMetadata());
            }
        }
    }

    private Field getPrivateField(Field field) {
        field.setAccessible(true);
        return field;
    }
}
