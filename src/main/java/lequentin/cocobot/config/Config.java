package lequentin.cocobot.config;

import java.io.IOException;
import java.io.InputStream;

public class Config {

    private static Config instance = null;

    private Config() {
    }

    public static Config get() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Secrets secrets;

    public void readFromEnv() throws IOException {
        secrets = new Secrets();
        String botToken = System.getenv("BOT_TOKEN");
        if (botToken == null) {
            throw new RuntimeException("BOT_TOKEN env var not set!");
        }

        secrets.setBotToken(botToken);
    }

    public Secrets getSecrets() {
        return secrets;
    }

    // get a file from the resources folder
    // works everywhere, IDEA, unit test and JAR file.
    private InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }
}
