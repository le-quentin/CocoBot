package lequentin.cocobot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import lequentin.cocobot.config.Config;
import lequentin.cocobot.discord.DiscordConverter;
import lequentin.cocobot.discord.DiscordDirectAccessMessagesSource;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.storage.JsonFileMessagesRepository;
import lequentin.cocobot.storage.UserMessagesJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class SynchroniseStorageApplicationMain {

    private static Logger log = LoggerFactory.getLogger(SynchroniseStorageApplicationMain.class);

    private final MessagesSource externalSource;
    private final MessagesRepository storage;

    public SynchroniseStorageApplicationMain(MessagesSource externalSource, MessagesRepository storage) {
        this.externalSource = externalSource;
        this.storage = storage;
    }

    public static void main(final String[] args) {
        final Config config = loadConfig();

        final DiscordClient discordClient = DiscordClient.create(config.getSecrets().getBotToken());
        final GatewayDiscordClient gateway = discordClient.login().block();

        final DiscordConverter discordConverter = new DiscordConverter();
        final MessagesSource messagesSource = new DiscordDirectAccessMessagesSource(gateway, discordConverter);

        final UserMessagesJsonConverter jsonConverter = new UserMessagesJsonConverter();
        final MessagesRepository jsonStorage = new JsonFileMessagesRepository(
                Path.of(CocoApplicationMain.MESSAGES_FILE),
                JsonMapper.get(),
                jsonConverter
        );

        final SynchroniseStorageApplicationMain app = new SynchroniseStorageApplicationMain(messagesSource, jsonStorage);

        app.run();
    }

    public void run() {
        storage.synchronise(externalSource);
    }

    private static Config loadConfig() {
        try {
            return Config.readFromEnv(System::getenv);
        } catch(Exception ex) { log.error("There was an error reading config files");
            throw ex;
        }
    }
}
