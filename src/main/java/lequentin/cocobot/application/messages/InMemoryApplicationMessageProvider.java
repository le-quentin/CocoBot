package lequentin.cocobot.application.messages;

import lequentin.cocobot.config.Config;

import java.util.Map;

import static lequentin.cocobot.application.messages.ApplicationMessageCode.COMMAND_UNKNOWN;
import static lequentin.cocobot.application.messages.ApplicationMessageCode.HELP;
import static lequentin.cocobot.application.messages.ApplicationMessageCode.USER_NOT_FOUND;

public class InMemoryApplicationMessageProvider implements ApplicationMessageProvider {

    private final Map<ApplicationMessageCode, String> messageTemplates;

    public InMemoryApplicationMessageProvider(Config config) {
        messageTemplates = switch (config.getLanguage()) {
            case EN -> Map.of(
                    USER_NOT_FOUND, "I don't know the user {}",
                    COMMAND_UNKNOWN, "I don't know this command!",
                    HELP,
                    """
                    *kwaa kwaaaa* Coco is happy! Here are my commands:
                    ```
                    {prefix}me            - I impersonate you
                    {prefix}like John Doe - I impersonate user John Doe (username, not server alias!)
                    {prefix}help          - I show this help
                    ```
                    """.replaceAll("\\{prefix}", config.getPrefix())
            );
            case FR -> Map.of(
                    USER_NOT_FOUND, "Je ne connais pas l'utilisateur {}",
                    COMMAND_UNKNOWN, "Je ne connais pas cette commande !",
                    HELP,
                    """
                    *cuii cuiiii* Coco est content ! Voici mes commandes :
                    ```
                    {prefix}me            - Je t'imite
                    {prefix}like John Doe - J'imite l'utisateur John Doe (avec l'username, pas l'alias de serveur)
                    {prefix}help          - Je montre cette aide
                    ```
                    """.replaceAll("\\{prefix}", config.getPrefix())
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
