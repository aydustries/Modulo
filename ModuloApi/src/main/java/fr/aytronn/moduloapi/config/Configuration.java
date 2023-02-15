package fr.aytronn.moduloapi.config;

public class Configuration {
    private String mongoUri = "";
    private String botToken = "";
    private String serverId = "";

    /**
     * Get the mongo uri
     *
     * @return the mongo uri
     */
    public String getMongoUri() {
        return getEnv("MONGO_URI", this.mongoUri);
    }

    /**
     * Get the discord bot token
     *
     * @return the discord bot token
     */
    public String getBotToken() {
        return getEnv("BOT_TOKEN", this.botToken);
    }

    /**
     * Get the discord server id
     *
     * @return the discord server id
     */
    public String getServerId() {
        return getEnv("SERVER_ID", this.serverId);
    }

    /**
     * Get environment variable
     *
     * @param name          the name of the environment variable
     * @param defaultValue  the default value if the environment variable is not found
     *
     * @return the value of the environment variable or the default value
     */
    public String getEnv(String name, String defaultValue) {
        return System.getenv().getOrDefault(name.toUpperCase(), defaultValue);
    }
}
