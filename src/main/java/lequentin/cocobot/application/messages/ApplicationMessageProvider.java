package lequentin.cocobot.application.messages;

public interface ApplicationMessageProvider {
    String getMessage(ApplicationMessageCode messageCode, String... templateVariables);
}
