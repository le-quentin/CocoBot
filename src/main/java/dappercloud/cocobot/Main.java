package dappercloud.cocobot;

import dappercloud.cocobot.config.Config;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

import java.io.IOException;

public final class Main {
    public static void main(final String[] args) {

        final Config config = new Config();
        try {
            config.readFromResources();
        } catch(IOException ex) {
            System.err.println("There was an error reading config files");
            ex.printStackTrace(System.err);
            return;
        }

        final DiscordClient client = DiscordClient.create(config.getSecrets().getBotToken());
        final GatewayDiscordClient gateway = client.login().block();

        final MessageClient messageClient = new MessageClient();
        final CocoBot coco = new CocoBot(messageClient);

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            final Message message = event.getMessage();
            coco.handleMessage(message);
        });

        gateway.onDisconnect().block();
    }
}
