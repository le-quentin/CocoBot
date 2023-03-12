package lequentin.cocobot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lequentin.cocobot.application.CocoChatBotApplication;
import lequentin.cocobot.application.CocoCommandParser;
import lequentin.cocobot.application.ExcludeChatCommandsMessagesFilter;
import lequentin.cocobot.application.RemoveQuotesAndBlocksStringSanitizer;
import lequentin.cocobot.config.Config;
import lequentin.cocobot.discord.DiscordChatBotService;
import lequentin.cocobot.discord.DiscordConverter;
import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.domain.StringSanitizer;
import lequentin.cocobot.domain.StringTokenizer;
import lequentin.cocobot.domain.impersonator.LongImpersonationImpersonatorDecorator;
import lequentin.cocobot.domain.impersonator.MarkovImpersonator;
import lequentin.cocobot.domain.impersonator.MessagesFilterImpersonatorDecorator;
import lequentin.cocobot.domain.impersonator.MultipleSentencesImpersonatorDecorator;
import lequentin.cocobot.domain.markov.FindMaxOverBatchOfPathWalkerDecorator;
import lequentin.cocobot.domain.markov.MarkovChainsWalker;
import lequentin.cocobot.domain.markov.MarkovTokenizer;
import lequentin.cocobot.domain.markov.SimpleMarkovChainsWalker;
import lequentin.cocobot.domain.markov.WordsTuple;
import lequentin.cocobot.domain.sanitizer.SpacePunctuationSanitizer;
import lequentin.cocobot.domain.tokenizer.SanitizerStringTokenizerDecorator;
import lequentin.cocobot.domain.tokenizer.SentencesStringTokenizer;
import lequentin.cocobot.domain.tokenizer.WordsStringTokenizer;
import lequentin.cocobot.storage.JsonFileMessagesRepository;
import lequentin.cocobot.storage.UserMessagesJsonConverter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Random;

public class CocoApplicationMain {

    public static final String MESSAGES_FILE = "./data/messages.json";
    private final GatewayDiscordClient gatewayClient;
    private final DiscordChatBotService service;

    public CocoApplicationMain(GatewayDiscordClient gatewayClient, DiscordChatBotService service) {
        this.gatewayClient = gatewayClient;
        this.service = service;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        // Discord API
        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final GatewayDiscordClient gateway = discordClient.login().block();

        // discord package
        final DiscordConverter discordConverter = new DiscordConverter();

        // storage
        // If storage file does not exist, we synchronise first
        Path messagesFilePath = Path.of(MESSAGES_FILE);
        if (!Files.exists(messagesFilePath)) {
            System.out.println("messages.json file not found! Coco will generate it, and it will take a while");
            SynchroniseStorageApplicationMain.main(new String[]{});
        }

        // Build the storage objects
        final UserMessagesJsonConverter jsonConverter = new UserMessagesJsonConverter();
        final MessagesRepository messagesRepository = new JsonFileMessagesRepository(
                messagesFilePath,
                JsonMapper.get(),
                jsonConverter
        );

        // domain
        final StringSanitizer sanitizer = new RemoveQuotesAndBlocksStringSanitizer();
        final StringTokenizer sentencesStringTokenizer = new SentencesStringTokenizer(sanitizer);
        final WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        final StringTokenizer punctuationAwareWordsTokenizer = new SanitizerStringTokenizerDecorator(new SpacePunctuationSanitizer(), wordsTokenizer);
        final MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(punctuationAwareWordsTokenizer, 3);
//        final MarkovChainsWalker<WordsTuple> walker = new FindMaxOverBatchOfPathWalkerDecorator<>(
//                new SimpleMarkovChainsWalker<>(new Random()),
//                Comparator.comparingInt(MarkovPath::getNonDeterministicScore),
//                100,
//                2
//        );

        final MarkovChainsWalker<WordsTuple> leastDeterministicWalker = new FindMaxOverBatchOfPathWalkerDecorator<>(
                new SimpleMarkovChainsWalker<>(new Random()),
                Comparator.comparingInt(path -> (int)Math.round(path.getNonDeterministicScore() * (Math.log10(path.getLength())))),
                50,
                0
        );


//        final MarkovChainsWalker<WordsTuple> walker = new SimpleMarkovChainsWalker<>(new Random());
        final Impersonator markov3Impersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeChatCommandsMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, leastDeterministicWalker)
        );
        final Impersonator impersonator = new MultipleSentencesImpersonatorDecorator(
                new LongImpersonationImpersonatorDecorator(markov3Impersonator, 4, 5),
                2
        );

        // application
        final CocoCommandParser cocoCommandParser = new CocoCommandParser(impersonator);
        final CocoChatBotApplication coco = new CocoChatBotApplication(cocoCommandParser);

        // service
        final DiscordChatBotService service = new DiscordChatBotService(discordConverter, coco);

        // app
        final CocoApplicationMain app = new CocoApplicationMain(gateway, service);

        System.out.println("Loading all messages from repository...");
        impersonator.addAllMessagesFromSource(messagesRepository);
        System.out.println("All messages loaded!");
        app.run();
    }

    public void run() {
        service.subscribeToMessageCreateFlux(gatewayClient.on(MessageCreateEvent.class));
        System.out.println("Listening to new messages...");
        gatewayClient.onDisconnect().block();
    }

    private static Config loadConfig() {
        try {
            Config.get().readProperties(System::getenv);
        } catch(Exception ex) {
            System.err.println("There was an error reading config files");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
        return Config.get();
    }
}
