package dappercloud.cocobot;

import dappercloud.cocobot.config.Config;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.IOException;
import java.util.Random;

public class CocoApplication {

    private final GatewayDiscordClient gatewayClient;
    private final CocoFluxService service;

    public CocoApplication(GatewayDiscordClient gatewayClient, CocoFluxService service) {
        this.gatewayClient = gatewayClient;
        this.service = service;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final GatewayDiscordClient gateway = discordClient.login().block();

        final MessagesSource messagesSource = new DirectAccessMessagesSource(gateway);
        final Impersonator impersonator = new SimpleTokensRandomImpersonator(new SentencesTokenizer(), new Random());
        final MessageClient messageClient = new MessageClient();
        final CocoBot coco = new CocoBot(impersonator, messageClient);
        final CocoFluxService service = new CocoFluxService(coco);

        final CocoApplication app = new CocoApplication(gateway, service);

        impersonator.addAllMessagesFromSource(messagesSource);
        app.run();
    }

    public void run() {
        service.subscribeToMessageCreateFlux(gatewayClient.on(MessageCreateEvent.class));
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
