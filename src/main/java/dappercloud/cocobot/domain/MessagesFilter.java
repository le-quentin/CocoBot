package dappercloud.cocobot.domain;

@FunctionalInterface
public interface MessagesFilter {
    boolean accepts(Message msg);
}
