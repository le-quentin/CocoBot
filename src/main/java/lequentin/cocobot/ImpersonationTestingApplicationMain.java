package lequentin.cocobot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lequentin.cocobot.application.ImpersonationTestingChatBotApplication;
import lequentin.cocobot.config.Config;
import lequentin.cocobot.discord.DiscordChatBotService;
import lequentin.cocobot.discord.DiscordConverter;
import lequentin.cocobot.discord.MessageClient;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.storage.JsonFileMessagesRepository;
import lequentin.cocobot.storage.UserMessagesJsonConverter;

import java.nio.file.Path;

public class ImpersonationTestingApplicationMain {

    private final GatewayDiscordClient gatewayClient;
    private final DiscordChatBotService service;

    public ImpersonationTestingApplicationMain(GatewayDiscordClient gatewayClient, DiscordChatBotService service) {
        this.gatewayClient = gatewayClient;
        this.service = service;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        // Discord API
        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final GatewayDiscordClient gateway = discordClient.login().block();


        // storage
        final UserMessagesJsonConverter jsonConverter = new UserMessagesJsonConverter();
        final MessagesRepository messagesRepository = new JsonFileMessagesRepository(
                Path.of("messages.json"),
                JsonMapper.get(),
                jsonConverter
        );

        // discord package
        final DiscordConverter discordConverter = new DiscordConverter();
        final MessageClient messageClient = new MessageClient();

        // application
        final ImpersonationTestingChatBotApplication impersonationTestingApplication = new ImpersonationTestingChatBotApplication(messagesRepository);

        // service
        final DiscordChatBotService service = new DiscordChatBotService(discordConverter, impersonationTestingApplication, messageClient);

        // app
        final ImpersonationTestingApplicationMain app = new ImpersonationTestingApplicationMain(gateway, service);

        app.run();
    }

    public void run() {
        service.subscribeToMessageCreateFlux(gatewayClient.on(MessageCreateEvent.class));
        System.out.println("Listening to new messages...");
        gatewayClient.onDisconnect().block();
    }

    private static Config loadConfig() {
        try {
            Config.get().readFromEnv();
        } catch(Exception ex) {
            System.err.println("There was an error reading config files");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
        return Config.get();
    }
}
