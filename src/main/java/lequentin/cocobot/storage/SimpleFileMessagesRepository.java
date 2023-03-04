package lequentin.cocobot.storage;

import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.MessagesRepository;
import lequentin.cocobot.domain.MessagesSource;
import lequentin.cocobot.domain.User;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Deprecated(forRemoval = true, since = "Not storing messages timestamps")
public class SimpleFileMessagesRepository implements MessagesRepository {

    private enum State { BEGIN, OPEN_BRACKET, OPEN_QUOTE, CLOSE_QUOTE, COMMA, CLOSE_BRACKET, END };
    static class Parser {
        private BufferedReader reader;
        private User currentAuthor;

        private StateMachine stateMachine;

        public Parser(BufferedReader reader) {
            this.reader = reader;
            this.stateMachine = new StateMachine();
        }

        private User newAuthor() {
            try {
                String line = reader.readLine();
                if (line == null) return null;
                return new User(line);
            } catch (IOException e) {
                throw new RuntimeException("IO exception while parsing", e);
            }
        }

        public Message getNextMessage() {
            if (currentAuthor == null) {
                currentAuthor = newAuthor();
                if (currentAuthor == null) {
                    return null;
                }
            }

            Message message;
            do {
                stateMachine.next();
                message = stateMachine.popNewMessage();
                if (stateMachine.currentState == State.END) {
                    currentAuthor = newAuthor();
                    if (currentAuthor == null) {
                        return null;
                    }
                    stateMachine = new StateMachine();
                }
            } while(message == null);

            lastMessage = message.getText();
            return message;
        }

        private void throwMalformed() {
            throwMalformed(null);
        }

        private String lastMessage;
        private void throwMalformed(String msg) {
            if (msg != null) System.err.println(msg);
            System.err.printf("Current state: currentAuthor=%s, state=%s, lastMessage=%s", currentAuthor.getUsername(), stateMachine.currentState, lastMessage);
            throw new RuntimeException("Malformed messages file");
        }

        private class StateMachine {
            private State currentState = State.BEGIN;
            private Message newMessage = null;

            private Map<State, Runnable> transitions = Map.of(
                State.BEGIN, this::fromBegin,
                State.OPEN_BRACKET, this::fromOpenBracket,
                State.OPEN_QUOTE, this::fromOpenQuote,
                State.CLOSE_QUOTE, this::fromCloseQuote,
                State.COMMA, this::fromComma,
                State.CLOSE_BRACKET, this::fromCloseBracket,
                State.END, this::fromEnd
            );

            public void next() {
                transitions.get(currentState).run();
            }

            public Message popNewMessage() {
                Message toReturn = newMessage;
                newMessage = null;
                return toReturn;
            }

            private void fromBegin() {
                popFirstCharAndExpect('[');
                currentState = State.OPEN_BRACKET;
            }

            private void fromOpenBracket() {
                popFirstCharAndExpect('"');
                currentState = State.OPEN_QUOTE;
            }

            private void fromOpenQuote() {
                StringBuilder nextMessageText = new StringBuilder("\"");
                do {
                    nextMessageText.append(readUntilNextDoubleQuote());
                } while(nextMessageText.charAt(nextMessageText.length()-2) == '\\');
                newMessage = new Message(currentAuthor, null, deserializeText(nextMessageText.toString()));
                currentState = State.CLOSE_QUOTE;
            }

            private void fromCloseQuote() {
                char c = readUnchecked();
                if (c == ',') {
                    currentState = State.COMMA;
                } else if (c == ']'){
                    currentState = State.CLOSE_BRACKET;
                } else {
                    throwMalformed();
                }
            }

            private void fromComma() {
                popFirstCharAndExpect('"');
                currentState = State.OPEN_QUOTE;
            }

            private void fromCloseBracket() {
                popFirstCharAndExpect('\n');
                currentState = State.END;
            }

            private void fromEnd() {
                throw new RuntimeException("Error in code, shouldn't transition from END state");
            }

            private String readUntilNextDoubleQuote() {
                return readUntil('"');
            }

            private String readUntil(char car) {
                StringBuilder builder = new StringBuilder();
                char c;
                do {
                    c = readUnchecked();
                    builder.append(c);
                } while(c != car);
                return builder.toString();
            }

            private char popFirstCharAndExpect(char car) {
                char readChar = readUnchecked();
                if (car != readChar) throwMalformed(String.format("wanted %c but got %c", car, readChar));
                return car;
            }

            private char readUnchecked() {
                try {
                    char c = (char)reader.read();
//                    System.out.printf("%s %c%n", currentState, c);
                    return c;
                } catch (IOException e) {
                    throw new RuntimeException("IO Exception while reading", e);
                }
            }
        }
    }
    @Override
    public Flux<Message> getAllMessages() {
        try {
            BufferedReader reader = Files.newBufferedReader(
                    FileSystems.getDefault().getPath("./stored_messages"));
            Parser parser = new Parser(reader);
            return Flux.generate(generator -> {
                Message nextMessage = parser.getNextMessage();
                if(nextMessage != null) {
                    generator.next(nextMessage);
                } else {
                    generator.complete();
                }
            });
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException("Can't read stored_messages text file", e);
        }
    }

    @Override
    public void synchronise(MessagesSource externalSource) {
        Map<User, Collection<Message>> messagesByUser = externalSource
            .getAllMessages()
            .collectMultimap(Message::getAuthor)
            .block();

        ;
        try(BufferedWriter writer = Files.newBufferedWriter(
                FileSystems.getDefault().getPath("./stored_messages"),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Entry<User, Collection<Message>> entry : messagesByUser.entrySet()) {
                User user = entry.getKey();
                String messagesStr = entry.getValue()
                        .stream()
                        .map(Message::getText)
                        .map(SimpleFileMessagesRepository::serializeText)
                        .collect(Collectors.joining(","));
                writer.append(user.getUsername());
                writer.newLine();
                writer.append(String.format("[%s]", messagesStr));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Couldn't synchronise to file");
            e.printStackTrace(System.err);
        }
    }

    private static String serializeText(String text) {
        String escaped = text.replaceAll(Pattern.quote("\""), "\\\\\"");

        System.out.println(escaped);
        return String.format("\"%s\"", escaped);
    }

    private static String deserializeText(String serialized) {
        return serialized
            .substring(1, serialized.length() - 1)
            .replaceAll(Pattern.quote("\\\""), "\"");
    }
}
