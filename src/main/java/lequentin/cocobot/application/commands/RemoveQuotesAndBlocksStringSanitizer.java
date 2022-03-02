package lequentin.cocobot.application.commands;

import lequentin.cocobot.domain.StringSanitizer;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RemoveQuotesAndBlocksStringSanitizer implements StringSanitizer {
    @Override
    public String sanitize(String text) {
        String withoutBlocks = text.replaceAll("```(.*\\n?)*```", "");
        String result = Arrays.stream(withoutBlocks.split("\n"))
                .filter(line -> !line.startsWith(">"))
                .collect(Collectors.joining("\n"));
        if (!result.equals(text)) {
            System.out.println("Sanitized [" + text + "] into [" + result + "]");
        }
        return result;
    }
}
