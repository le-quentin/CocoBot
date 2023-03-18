package lequentin.cocobot.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lequentin.cocobot.JsonMapper;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class JsonFileMessagesRepositoryIntTest {

    private final Instant CREATED_AT_1 = Instant.parse("2022-01-04T19:30:42Z");
    private final Instant CREATED_AT_2 = Instant.parse("2022-02-01T02:01:11Z");

    private final ObjectMapper objectMapper = JsonMapper.get();

    @Test
    void shouldGetAllMessages() {
        JsonFileMessagesRepository repository = new JsonFileMessagesRepository(
                getResourcePath("storage.json"),
                objectMapper,
                new UserMessagesJsonConverter()
        );
        Flux<Message> allMessages = repository.getAllMessages();

        User author1 = new User("toto");
        User author2 = new User("tata");
        StepVerifier.create(allMessages)
                .assertNext(msg -> assertMessage(msg, author1, CREATED_AT_1, "Hi guys"))
                .assertNext(msg -> assertMessage(msg, author1, CREATED_AT_2, "How are you?"))
                .assertNext(msg -> assertMessage(msg, author2, CREATED_AT_1, "Hi girls"))
                .assertNext(msg -> assertMessage(msg, author2, CREATED_AT_2, "How are you not?"))
                .verifyComplete();
    }

    @Test
    void shouldSynchronise() throws IOException {
        File tempFile = File.createTempFile("int-test-output_jsonFileRepo_synchronise", "json");
        Path filePath = Path.of(tempFile.getAbsolutePath());
        JsonFileMessagesRepository repository = new JsonFileMessagesRepository(
                filePath,
                objectMapper,
                new UserMessagesJsonConverter()
        );
        User author1 = new User("titi");
        User author2 = new User("tutu");
        Flux<Message> source = Flux.just(
                new Message(author1, CREATED_AT_1, "Hello there"),
                new Message(author1, CREATED_AT_2, "How are you?"),
                new Message(author2,  CREATED_AT_2,"I'm doing fine")
        );

        repository.synchronise(() -> source);

        String content = Files.readString(filePath);
        String user1Json = "{" +
            "\"username\":\"titi\"," +
            "\"messages\":[" +
                "{\"createdAt\":\"2022-01-04T19:30:42Z\",\"text\":\"Hello there\"}," +
                "{\"createdAt\":\"2022-02-01T02:01:11Z\",\"text\":\"How are you?\"}" +
            "]" +
        "}";
        String user2Json = "{" +
            "\"username\":\"tutu\"," +
            "\"messages\":[" +
                "{\"createdAt\":\"2022-02-01T02:01:11Z\",\"text\":\"I'm doing fine\"}" +
            "]" +
        "}";
        assertThat(content).isIn(
                "["+user1Json+","+user2Json+"]",
                "["+user2Json+","+user1Json+"]"
        );
    }

    private void assertMessage(Message message, User author, Instant createdAt, String text) {
        assertThat(message).usingRecursiveComparison().isEqualTo(new Message(author, createdAt, text));
    }

    private Path getResourcePath(String relativePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return Path.of(classLoader.getResource(relativePath).getPath());
    }

}