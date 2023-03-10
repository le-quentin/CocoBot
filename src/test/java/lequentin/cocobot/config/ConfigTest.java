package lequentin.cocobot.config;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThatThrownBy(() -> Config.get().readFromEnv())
                .hasMessageContaining("BOT_TOKEN").hasMessageContaining("not set");
    }
}