package dappercloud.cocobot.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LongImpersonationImpersonatorDecorator implements Impersonator {

    private final Impersonator impersonator;
    private final int minimumWordsCount;
    private final int maximumAttempts;

    public LongImpersonationImpersonatorDecorator(Impersonator impersonator, int minimumWordsCount, int maximumAttempts) {
        this.impersonator = impersonator;
        this.minimumWordsCount = minimumWordsCount;
        this.maximumAttempts = maximumAttempts;
    }

    @Override
    public void addMessage(Message message) {
        impersonator.addMessage(message);
    }

    @Override
    public String impersonate(User user) {
        Map<String, Integer> impersonations = new HashMap<>();
        for (int i = 0; i < maximumAttempts; i++) {
            String newImpersonation = impersonator.impersonate(user);
            int wordsCount = newImpersonation.split(" ").length;
            System.out.println(i + ": " + newImpersonation);
            if (wordsCount >= minimumWordsCount) {
                return newImpersonation;
            }
            impersonations.put(newImpersonation, wordsCount);
        }

        // We never got a long enough impersonation, returning the longest one
        return impersonations.entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.comparingInt(a -> -a)))
                .max(Entry.comparingByValue())
                .orElseThrow(() -> new RuntimeException("Should not happen"))
                .getKey();
    }
}