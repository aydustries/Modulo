package fr.aytronn.moduloapi.config;

public class Configuration {
    private String mongoUri = "";
    private String botToken = "";

    public String getMongoUri() {
        return getEnv("MONGO_URI", this.mongoUri);
    }

    public String getBotToken() {
        return getEnv("BOT_TOKEN", this.botToken);
    }

    public String getEnv(String name, String value) {
        return System.getenv().getOrDefault(name.toUpperCase(), value);
    }
}
