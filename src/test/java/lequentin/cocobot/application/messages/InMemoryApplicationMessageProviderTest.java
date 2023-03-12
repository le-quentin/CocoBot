package lequentin.cocobot.application.messages;

import lequentin.cocobot.config.Language;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryApplicationMessageProviderTest {

    @Test
    void shouldGetMessagesInEnglish() {
        InMemoryApplicationMessageProvider provider = new InMemoryApplicationMessageProvider(Language.EN);

        String message = provider.getMessage(ApplicationMessageCode.USER_NOT_FOUND, "username");

        assertThat(message).contains("I don't know the user username");
    }
    
    @Test
    void shouldGetMessagesInFrench() {
        InMemoryApplicationMessageProvider provider = new InMemoryApplicationMessageProvider(Language.FR);

        String message = provider.getMessage(ApplicationMessageCode.USER_NOT_FOUND, "username");

        assertThat(message).contains("Je ne connais pas l'utilisateur username");
    }

}