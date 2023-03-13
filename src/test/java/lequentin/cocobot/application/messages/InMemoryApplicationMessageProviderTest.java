package lequentin.cocobot.application.messages;

import lequentin.cocobot.config.Config;
import lequentin.cocobot.config.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryApplicationMessageProviderTest {

    private Config config;
    private InMemoryApplicationMessageProvider provider;

    @BeforeEach
    void setUp() {
        config = mock(Config.class);
        when(config.getPrefix()).thenReturn("c/");
    }

    @Test
    void shouldGetMessagesInEnglish() {
        when(config.getLanguage()).thenReturn(Language.EN);
        InMemoryApplicationMessageProvider provider = new InMemoryApplicationMessageProvider(config);

        String message = provider.getMessage(ApplicationMessageCode.USER_NOT_FOUND, "username");

        assertThat(message).contains("I don't know the user username");
    }
    
    @Test
    void shouldGetMessagesInFrench() {
        when(config.getLanguage()).thenReturn(Language.FR);
        InMemoryApplicationMessageProvider provider = new InMemoryApplicationMessageProvider(config);

        String message = provider.getMessage(ApplicationMessageCode.USER_NOT_FOUND, "username");

        assertThat(message).contains("Je ne connais pas l'utilisateur username");
    }

}