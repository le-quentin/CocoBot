package lequentin.cocobot;

import lequentin.cocobot.config.Config;
import lequentin.cocobot.discord.DirectAccessMessagesSource;
import lequentin.cocobot.discord.DiscordConverter;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.storage.SimpleFileMessagesRepository;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;

import java.io.IOException;

public class SynchroniseStorageApplication {

    private final MessagesSource externalSource;
    private final MessagesRepository storage;

    public SynchroniseStorageApplication(MessagesSource externalSource, MessagesRepository storage) {
        this.externalSource = externalSource;
        this.storage = storage;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final GatewayDiscordClient gateway = discordClient.login().block();

        final DiscordConverter discordConverter = new DiscordConverter();
        final MessagesSource messagesSource = new DirectAccessMessagesSource(gateway, discordConverter);
        final MessagesRepository storage = new SimpleFileMessagesRepository();

        final SynchroniseStorageApplication app = new SynchroniseStorageApplication(messagesSource, storage);

        app.run();
    }

    public void run() {
        storage.synchronise(externalSource);
    }

    private static Config loadConfig() {
        try {
            Config.get().readFromEnv();
        } catch(IOException ex) {
            System.err.println("There was an error reading config files");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
        return Config.get();
    }
}