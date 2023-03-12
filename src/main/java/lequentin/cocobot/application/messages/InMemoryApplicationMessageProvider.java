package lequentin.cocobot.application.messages;

import lequentin.cocobot.config.Language;

import java.util.Map;

import static lequentin.cocobot.application.messages.ApplicationMessageCode.COMMAND_UNKNOWN;
import static lequentin.cocobot.application.messages.ApplicationMessageCode.USER_NOT_FOUND;

public class InMemoryApplicationMessageProvider implements ApplicationMessageProvider {

    private final Map<ApplicationMessageCode, String> messageTemplates;

    public InMemoryApplicationMessageProvider(Language language) {
        messageTemplates = switch (language) {
            case EN -> Map.of(
                    USER_NOT_FOUND, "I don't know the user {}",
                    COMMAND_UNKNOWN, "I don't know this command!"
            );
            case FR -> Map.of(
                    USER_NOT_FOUND, "Je ne connais pas l'utilisateur {}",
                    COMMAND_UNKNOWN, "Je ne connais pas cette commande !"
            );
        };
    }

    @Override
    public String getMessage(ApplicationMessageCode messageCode, String... templateVariables) {
        return replaceTemplatePlaceholders(messageTemplates.get(messageCode), templateVariables);
    }

    private String replaceTemplatePlaceholders(String template, String... templateVariables) {
        return String.format(template.replaceAll("\\{}", "%s"), (Object[]) templateVariables);
    }


}
