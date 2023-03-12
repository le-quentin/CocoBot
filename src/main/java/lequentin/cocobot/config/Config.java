package lequentin.cocobot.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class Config {

    private Secrets secrets;
    private Language language;

    public static final Scanner INPUT_SCANNER = new Scanner(System.in);

    private static Config instance = null;

    private Config() {
        defaultConfig();
    }

    private void defaultConfig() {
        language = Language.EN;
    }

    public static Config get() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void readProperties(PropertiesProvider propertiesProvider) {
        readProperties(propertiesProvider, false);
    }

    public void readProperties(PropertiesProvider propertiesProvider, boolean promptFallback) {
        secrets = new Secrets();
        String botToken = propertiesProvider.getProperty("BOT_TOKEN");
        if (StringUtils.isBlank(botToken)) {
            if (!promptFallback) throw new RuntimeException("BOT_TOKEN env var not set!");
            System.out.println("PLEASE PROVIDE BOT_TOKEN");
            botToken = INPUT_SCANNER.nextLine();
        }

        String languageString = propertiesProvider.getProperty("LANGUAGE");
        if (StringUtils.isNotBlank(languageString)) {
            language = Language.valueOf(languageString.toUpperCase());
        }

        secrets.setBotToken(botToken);
    }

    public Secrets getSecrets() {
        return secrets;
    }

    public Language getLanguage() {
        return language;
    }

    @FunctionalInterface
    public interface PropertiesProvider {
        String getProperty(String propertyName);
    }
}
