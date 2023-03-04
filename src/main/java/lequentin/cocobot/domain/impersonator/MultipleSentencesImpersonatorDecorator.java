package lequentin.cocobot.domain.impersonator;

import lequentin.cocobot.domain.Impersonator;
import lequentin.cocobot.domain.Message;
import lequentin.cocobot.domain.User;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultipleSentencesImpersonatorDecorator implements Impersonator {

    private final Impersonator impersonator;
    private final int numberOfSentences;

    public MultipleSentencesImpersonatorDecorator(Impersonator impersonator, int numberOfSentences) {
        this.impersonator = impersonator;
        this.numberOfSentences = numberOfSentences;
    }

    @Override
    public void addMessage(Message message) {
        impersonator.addMessage(message);
    }

    @Override
    public String impersonate(User user) {
        return IntStream.range(0, numberOfSentences)
                .mapToObj(i -> impersonator.impersonate(user))
                .collect(Collectors.joining(". "));
    }
}
