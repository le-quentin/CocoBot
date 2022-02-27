package dappercloud.cocobot;

import dappercloud.cocobot.application.CocoChatBotApplication;
import dappercloud.cocobot.application.CocoCommandParser;
import dappercloud.cocobot.config.Config;
import dappercloud.cocobot.discord.DiscordChatBotService;
import dappercloud.cocobot.discord.DiscordConverter;
import dappercloud.cocobot.discord.ExcludeCommandsDiscordMessagesFilter;
import dappercloud.cocobot.discord.MessageClient;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MarkovImpersonator;
import dappercloud.cocobot.domain.MessagesFilterImpersonatorDecorator;
import dappercloud.cocobot.domain.MessagesRepository;
import dappercloud.cocobot.domain.MultipleSentencesImpersonatorDecorator;
import dappercloud.cocobot.domain.SentencesStringTokenizer;
import dappercloud.cocobot.domain.StringTokenizer;
import dappercloud.cocobot.domain.WordsStringTokenizer;
import dappercloud.cocobot.domain.markov.FindMaxOverBatchOfPathWalkerDecorator;
import dappercloud.cocobot.domain.markov.MarkovChainsWalker;
import dappercloud.cocobot.domain.markov.MarkovPath;
import dappercloud.cocobot.domain.markov.MarkovTokenizer;
import dappercloud.cocobot.domain.markov.SimpleMarkovChainsWalker;
import dappercloud.cocobot.domain.markov.WordsTuple;
import dappercloud.cocobot.storage.SimpleFileMessagesRepository;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.IOException;
import java.util.Comparator;
import java.util.Random;

public class CocoApplication {

    private final GatewayDiscordClient gatewayClient;
    private final DiscordChatBotService service;

    public CocoApplication(GatewayDiscordClient gatewayClient, DiscordChatBotService service) {
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
        final MessagesRepository messagesRepository = new SimpleFileMessagesRepository();
        final MessageClient messageClient = new MessageClient();

        // domain
        final StringTokenizer sentencesStringTokenizer = new SentencesStringTokenizer();
        final WordsStringTokenizer wordsTokenizer = new WordsStringTokenizer();
        final MarkovTokenizer markov3Tokenizer = new MarkovTokenizer(wordsTokenizer, 3);
        final MarkovChainsWalker<WordsTuple> walker = new FindMaxOverBatchOfPathWalkerDecorator<>(
                new SimpleMarkovChainsWalker<>(new Random()),
                Comparator.comparingInt(MarkovPath::getNonDeterministicScore),
                100,
                2
        );
        final Impersonator markovImpersonator = new MessagesFilterImpersonatorDecorator(
                new ExcludeCommandsDiscordMessagesFilter(),
                new MarkovImpersonator(sentencesStringTokenizer, markov3Tokenizer, walker)
        );
        final Impersonator impersonator = new MultipleSentencesImpersonatorDecorator(
                markovImpersonator,
                2
        );


        // application
        final CocoCommandParser cocoCommandParser = new CocoCommandParser();
        final CocoChatBotApplication coco = new CocoChatBotApplication(impersonator, cocoCommandParser);

        // service
        final DiscordChatBotService service = new DiscordChatBotService(discordConverter, coco, messageClient);

        // app
        final CocoApplication app = new CocoApplication(gateway, service);

        System.out.println("Loading all messages from repository...");
        impersonator.addAllMessagesFromSource(messagesRepository);
        System.out.println("Messages read!");
        app.run();
    }

    public void run() {
        service.subscribeToMessageCreateFlux(gatewayClient.on(MessageCreateEvent.class));
        System.out.println("Listening to new messages...");
        gatewayClient.onDisconnect().block();
    }

    private static Config loadConfig() {
        try {
            Config.get().readFromResources();
        } catch(IOException ex) {
            System.err.println("There was an error reading config files");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
        return Config.get();
    }
}
