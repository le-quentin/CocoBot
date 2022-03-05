package lequentin.cocobot.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class JsonFileMessagesRepositoryIntTest {

    @Test
    void shouldGetAllMessages() {
        JsonFileMessagesRepository repository = new JsonFileMessagesRepository(
                getResourcePath("storage.json"),
                new ObjectMapper(),
                new UserMessagesJsonConverter()
        );
        Flux<Message> allMessages = repository.getAllMessages();

        User author1 = new User("toto");
        User author2 = new User("tata");
        StepVerifier.create(allMessages)
                .assertNext(msg -> assertMessage(msg, author1, "Hi guys"))
                .assertNext(msg -> assertMessage(msg, author1, "How are you?"))
                .assertNext(msg -> assertMessage(msg, author2, "Hi girls"))
                .assertNext(msg -> assertMessage(msg, author2, "How are you not?"))
                .verifyComplete();
    }

    @Test
    void shouldSynchronise() throws IOException {
        Path filePath = getResourceFolderPath().resolve("build/intTest_jsonFileRepo_synchronise.json");
        if (Files.exists(filePath)) Files.delete(filePath);
        JsonFileMessagesRepository repository = new JsonFileMessagesRepository(
                filePath,
                new ObjectMapper(),
                new UserMessagesJsonConverter()
        );
        User author1 = new User("titi");
        User author2 = new User("tutu");
        Flux<Message> source = Flux.just(
                new Message(author1, "Hello there"),
                new Message(author1, "How are you?"),
                new Message(author2, "I'm doing fine")
        );

        repository.synchronise(() -> source);

        String content = Files.readString(filePath);
        String user1Json = "{" +
            "\"username\":\"titi\"," +
            "\"messages\":[" +
                "{\"text\":\"Hello there\"}," +
                "{\"text\":\"How are you?\"}" +
            "]" +
        "}";
        String user2Json = "{" +
            "\"username\":\"tutu\"," +
            "\"messages\":[" +
                "{\"text\":\"I'm doing fine\"}" +
            "]" +
        "}";
        assertThat(content).isIn(
                "["+user1Json+","+user2Json+"]",
                "["+user2Json+","+user1Json+"]"
        );
    }

    private void assertMessage(Message message, User author, String text) {
        assertThat(message).usingRecursiveComparison().isEqualTo(new Message(author, text));
    }

    private Path getResourcePath(String relativePath) {
        ClassLoader classLoader = getClass().getClassLoader();
        return Path.of(classLoader.getResource(relativePath).getPath());
    }

    private Path getResourceFolderPath() {
        return getResourcePath("storage.json").getParent();
    }
}