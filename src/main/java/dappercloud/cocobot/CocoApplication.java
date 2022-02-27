package dappercloud.cocobot;

import dappercloud.cocobot.application.CocoCommandParser;
import dappercloud.cocobot.config.Config;
import dappercloud.cocobot.discord.DiscordChatBotService;
import dappercloud.cocobot.discord.DiscordConverter;
import dappercloud.cocobot.discord.ExcludeCommandsDiscordMessagesFilter;
import dappercloud.cocobot.discord.MessageClient;
import dappercloud.cocobot.application.CocoChatBotApplication;
import dappercloud.cocobot.domain.Impersonator;
import dappercloud.cocobot.domain.MessagesFilter;
import dappercloud.cocobot.domain.MessagesFilterImpersonatorDecorator;
import dappercloud.cocobot.domain.MessagesRepository;
import dappercloud.cocobot.domain.SentencesStringTokenizer;
import dappercloud.cocobot.domain.SimpleTokensRandomImpersonator;
import dappercloud.cocobot.storage.SimpleFileMessagesRepository;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.IOException;
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
        final MessagesFilter discordMessagesFilter = new ExcludeCommandsDiscordMessagesFilter();

        // domain
        final Impersonator impersonator = new SimpleTokensRandomImpersonator(new SentencesStringTokenizer(), new Random());
        final Impersonator filteredImpersonator = new MessagesFilterImpersonatorDecorator(discordMessagesFilter, impersonator);

        // application
        final CocoCommandParser cocoCommandParser = new CocoCommandParser();
        final CocoChatBotApplication coco = new CocoChatBotApplication(filteredImpersonator, cocoCommandParser);

        // service
        final DiscordChatBotService service = new DiscordChatBotService(discordConverter, coco, messageClient);

        // app
        final CocoApplication app = new CocoApplication(gateway, service);

        System.out.println("Loading all messages from repostitory...");
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
