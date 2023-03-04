package lequentin.cocobot.config;

import java.io.InputStream;
import java.util.Scanner;

public class Config {

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

    private Secrets secrets;

    public void readFromEnv() {
        readFromEnv(false);
    }

    public void readFromEnv(boolean promptFallback) {
        secrets = new Secrets();
        String botToken = System.getenv("BOT_TOKEN");
        if (botToken == null) {
            if (!promptFallback) throw new RuntimeException("BOT_TOKEN env var not set!");
            System.out.println("PLEASE PROVIDE BOT_TOKEN");
            botToken = INPUT_SCANNER.nextLine();
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
