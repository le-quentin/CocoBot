package dappercloud.cocobot;

import dappercloud.cocobot.config.Config;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

import java.io.IOException;

public class CocoApplication {

    private final DiscordClient discordClient;
    private final CocoBot coco;

    public CocoApplication(DiscordClient discordClient, CocoBot coco) {
        this.discordClient = discordClient;
        this.coco = coco;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        final MessageClient messageClient = new MessageClient();
        final CocoBot coco = new CocoBot(messageClient);

        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final CocoApplication app = new CocoApplication(discordClient, coco);
        app.run();
    }

    public void run() {
        final GatewayDiscordClient gateway = discordClient.login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            coco.handleMessage(message);
        });

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
