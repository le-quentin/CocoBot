package lequentin.cocobot.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.domain.MessagesSource;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonFileMessagesRepository implements MessagesRepository {

    private final Path filePath;
    private final ObjectMapper objectMapper;
    private final UserMessagesJsonConverter converter;

    public JsonFileMessagesRepository(Path filePath, ObjectMapper objectMapper, UserMessagesJsonConverter converter) {
        this.filePath = filePath;
        this.objectMapper = objectMapper;
        this.converter = converter;
    }

    @Override
    public Flux<Message> getAllMessages() {
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UserMessagesJson[] usersJson;
        try {
            usersJson = objectMapper.readValue(content, UserMessagesJson[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return Flux.create(emitter -> {
            Arrays.stream(usersJson).forEach(user -> {
               user.getMessages().forEach(messageJson -> emitter.next(converter.toDomainMessage(user, messageJson)));
            });
            emitter.complete();
        });
    }

    @Override
    public void synchronise(MessagesSource externalSource) {
        UserMessagesJson[] usersMessagesJson = externalSource.getAllMessages()
                .collectMultimap(Message::getAuthor)
                .block()
                .entrySet().stream()
                .map(entry -> new UserMessagesJson(
                        entry.getKey().getUsername(),
                        entry.getValue().stream().map(converter::toJsonMessage).collect(Collectors.toList())))
                .toArray(UserMessagesJson[]::new);

        String content;
        try {
            content = objectMapper.writeValueAsString(usersMessagesJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
