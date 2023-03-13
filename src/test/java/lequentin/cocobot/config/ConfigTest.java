package lequentin.cocobot.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigTest {

    @Test
    void shouldNotReadConfigFromEnvWhenBotTokenNotSet() {
        assertThatThrownBy(() -> Config.readFromEnv(propertyName -> ""))
                .hasMessageContaining("COCOBOT_TOKEN").hasMessageContaining("not set");
    }

    @Test
    void shouldReadConfigFromEnvWithOnlyRequiredVars() {
        Config.PropertiesProvider propertiesProvider = mock(Config.PropertiesProvider.class);
        when(propertiesProvider.getProperty("COCOBOT_TOKEN")).thenReturn("adummytoken");

        Config config = Config.readFromEnv(propertiesProvider);

        assertThat(config.getSecrets().getBotToken()).isEqualTo("adummytoken");
        assertThat(config.getLanguage()).isEqualTo(Language.EN);
    }

    @EnumSource(value = Language.class)
    @ParameterizedTest
    void shouldReadConfigFromEnvWithAllVars(Language language) {
        Config.PropertiesProvider propertiesProvider = mock(Config.PropertiesProvider.class);
        when(propertiesProvider.getProperty("COCOBOT_TOKEN")).thenReturn("adummytoken");
        when(propertiesProvider.getProperty("COCOBOT_LANGUAGE")).thenReturn(language.name().toLowerCase());

        Config config = Config.readFromEnv(propertiesProvider);

        assertThat(config.getSecrets().getBotToken()).isEqualTo("adummytoken");
        assertThat(config.getLanguage()).isEqualTo(language);
    }

}