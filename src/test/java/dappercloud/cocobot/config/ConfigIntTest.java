package dappercloud.cocobot.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class ConfigIntTest {

    @Test
    void shouldGetSecretsFromConfig() throws IOException {
        Config config = Config.get();
        config.readFromResources();

        assertThat(config.getSecrets()).isNotNull();
        assertThat(config.getSecrets().getBotToken()).isNotBlank();
    }

}