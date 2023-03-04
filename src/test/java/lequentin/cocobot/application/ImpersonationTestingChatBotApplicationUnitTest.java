package lequentin.cocobot.application;

import lequentin.cocobot.JsonMapper;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.storage.JsonFileMessagesRepository;
import lequentin.cocobot.storage.UserMessagesJsonConverter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

@Disabled("This is used only for exploratory testing on a give messages file")
class ImpersonationTestingChatBotApplicationUnitTest {

    @Test
    void shouldSample() {
        final UserMessagesJsonConverter jsonConverter = new UserMessagesJsonConverter();
        final MessagesRepository messagesRepository = new JsonFileMessagesRepository(
                Path.of("messages.json"),
                JsonMapper.get(),
                jsonConverter
        );
        final ImpersonationTestingChatBotApplication app = new ImpersonationTestingChatBotApplication(messagesRepository);

        app.sample();
    }

    @Test
    void shouldCompareChainsMetadata() throws Exception {
        final UserMessagesJsonConverter jsonConverter = new UserMessagesJsonConverter();
        final MessagesRepository messagesRepository = new JsonFileMessagesRepository(
                Path.of("messages.json"),
                JsonMapper.get(),
                jsonConverter
        );
        final ImpersonationTestingChatBotApplication app = new ImpersonationTestingChatBotApplication(messagesRepository);

        app.printChainsMetadata();
    }
}