package dappercloud.cocobot;

import dappercloud.cocobot.config.Config;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.io.IOException;

public class CocoApplication {

    private final DiscordClient discordClient;
    private final CocoFluxService service;

    public CocoApplication(DiscordClient discordClient, CocoFluxService service) {
        this.discordClient = discordClient;
        this.service = service;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        final MessageClient messageClient = new MessageClient();
        final CocoBot coco = new CocoBot(messageClient);
        final CocoFluxService service = new CocoFluxService(coco);

        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final CocoApplication app = new CocoApplication(discordClient, service);
        app.run();
    }

    public void run() {
        final GatewayDiscordClient gateway = discordClient.login().block();
        service.subscribeToMessageCreateFlux(gateway.on(MessageCreateEvent.class));
        gateway.onDisconnect().block();
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
