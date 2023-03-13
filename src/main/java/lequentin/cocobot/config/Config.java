package lequentin.cocobot.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class Config {

    private final Secrets secrets;
    private final Language language;

    public static final Scanner INPUT_SCANNER = new Scanner(System.in);

    public Config(Secrets secrets, Language language) {
        this.secrets = secrets;
        this.language = language;
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

    public static Config readFromEnv(PropertiesProvider propertiesProvider) {
        return readFromEnv(propertiesProvider, false);
    }

    public static Config readFromEnv(PropertiesProvider propertiesProvider, boolean promptFallback) {
        Builder builder = new Builder();

        String botToken = propertiesProvider.getProperty("BOT_TOKEN");
        if (StringUtils.isBlank(botToken)) {
            if (!promptFallback) throw new RuntimeException("BOT_TOKEN env var not set!");
            System.out.println("PLEASE PROVIDE BOT_TOKEN");
            botToken = INPUT_SCANNER.nextLine();
        }
        builder.secrets(new Secrets(botToken));

        String languageString = propertiesProvider.getProperty("LANGUAGE");
        if (StringUtils.isNotBlank(languageString)) {
            builder.language(Language.valueOf(languageString.toUpperCase()));
        }

        return builder.build();
    }

    private static class Builder {

        private Secrets secrets;
        private Language language;

        private Builder() {
            language = Language.EN;
        }

        public Builder secrets(Secrets secrets) {
            this.secrets = secrets;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Config build() {
            return new Config(secrets, language);
        }
    }
}
