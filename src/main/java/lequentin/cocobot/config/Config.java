package lequentin.cocobot.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Config {

    private static Logger log = LoggerFactory.getLogger(Config.class);

    private final Secrets secrets;
    private final Language language;
    private final String prefix;

    public static final Scanner INPUT_SCANNER = new Scanner(System.in);

    public Config(Secrets secrets, Language language, String prefix) {
        this.secrets = secrets;
        this.language = language;
        this.prefix = prefix;
    }

    public Secrets getSecrets() {
        return secrets;
    }

    public Language getLanguage() {
        return language;
    }

    public String getPrefix() {
        return prefix;
    }

    public static Config readFromEnv(PropertiesProvider propertiesProvider) {
        return readFromEnv(propertiesProvider, false);
    }

    public static Config readFromEnv(PropertiesProvider propertiesProvider, boolean promptFallback) {
        Builder builder = new Builder();

        String botToken = propertiesProvider.getProperty("COCOBOT_TOKEN");
        if (StringUtils.isBlank(botToken)) {
            if (!promptFallback) throw new RuntimeException("COCOBOT_TOKEN env var not set!");
            log.info("Please provide COCOBOT_TOKEN");
            botToken = INPUT_SCANNER.nextLine();
        }
        builder.secrets(new Secrets(botToken));

        String languageString = propertiesProvider.getProperty("COCOBOT_LANGUAGE");
        if (StringUtils.isNotBlank(languageString)) {
            builder.language(Language.valueOf(languageString.toUpperCase()));
        }

        String prefixString = propertiesProvider.getProperty("COCOBOT_PREFIX");
        if (StringUtils.isNotBlank(prefixString)) {
            builder.prefix(prefixString);
        }

        return builder.build();
    }

    @FunctionalInterface
    public interface PropertiesProvider {
        String getProperty(String propertyName);
    }

    private static class Builder {

        private Secrets secrets;
        private Language language;
        private String prefix;

        private Builder() {
            language = Language.EN;
            prefix = "c/";
        }

        public Builder secrets(Secrets secrets) {
            this.secrets = secrets;
            return this;
        }

        public Builder language(Language language) {
            this.language = language;
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Config build() {
            return new Config(secrets, language, prefix);
        }
    }

}
