package lequentin.cocobot.application;

import lequentin.cocobot.JsonMapper;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.storage.JsonFileMessagesRepository;
import lequentin.cocobot.storage.UserMessagesJsonConverter;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class ImpersonationTestingChatBotApplicationUnitTest {

    @Test
    void shouldSample() {
        final UserMessagesJsonConverter jsonConverter = new UserMessagesJsonConverter();
        final MessagesRepository messagesRepository = new JsonFileMessagesRepository(
                Path.of("stored_messages.json"),
                JsonMapper.get(),
                jsonConverter
        );
        final ImpersonationTestingChatBotApplication app = new ImpersonationTestingChatBotApplication(messagesRepository);

        app.sample();
    }
}