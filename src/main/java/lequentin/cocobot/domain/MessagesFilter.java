package lequentin.cocobot.domain;

@FunctionalInterface
public interface MessagesFilter {
    boolean accepts(Message msg);
}
