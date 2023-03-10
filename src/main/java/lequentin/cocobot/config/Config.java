package lequentin.cocobot.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class Config {

    private Secrets secrets;

    public static final Scanner INPUT_SCANNER = new Scanner(System.in);

    private static Config instance = null;

    private Config() {
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

        secrets.setBotToken(botToken);
    }

    public Secrets getSecrets() {
        return secrets;
    }

    @FunctionalInterface
    public interface PropertiesProvider {
        String getProperty(String propertyName);
    }
}
