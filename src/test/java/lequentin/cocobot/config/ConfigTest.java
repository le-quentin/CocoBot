package lequentin.cocobot.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigTest {

    @Test
    void shouldGetSameSameInstanceAlways() throws ExecutionException, InterruptedException {
        Config config = Config.get();
        CompletableFuture<Config> asyncResult = new CompletableFuture<>();
        Thread thread = new Thread(() -> asyncResult.complete(Config.get()));

        thread.start();

        Config otherThreadConfig = asyncResult.get();
        assertThat(otherThreadConfig).isSameAs(config);
    }

    @Test
    void shouldNotReadConfigFromEnvWhenBotTokenNotSet() {
        assertThatThrownBy(() -> Config.get().readProperties(propertyName -> ""))
                .hasMessageContaining("BOT_TOKEN").hasMessageContaining("not set");
    }

    @Test
    void shouldReadConfigFromEnvWithOnlyRequiredVars() {
        Config.PropertiesProvider propertiesProvider = mock(Config.PropertiesProvider.class);
        when(propertiesProvider.getProperty("BOT_TOKEN")).thenReturn("adummytoken");

        Config.get().readProperties(propertiesProvider);

        assertThat(Config.get().getSecrets().getBotToken()).isEqualTo("adummytoken");
        assertThat(Config.get().getLanguage()).isEqualTo(Language.EN);
    }

    @EnumSource(value = Language.class)
    @ParameterizedTest
    void shouldReadConfigFromEnvWithAllVars(Language language) {
        Config.PropertiesProvider propertiesProvider = mock(Config.PropertiesProvider.class);
        when(propertiesProvider.getProperty("BOT_TOKEN")).thenReturn("adummytoken");
        when(propertiesProvider.getProperty("LANGUAGE")).thenReturn(language.name().toLowerCase());

        Config.get().readProperties(propertiesProvider);

        assertThat(Config.get().getSecrets().getBotToken()).isEqualTo("adummytoken");
        assertThat(Config.get().getLanguage()).isEqualTo(language);
    }
}