package fr.aytronn.moduloapi.mongodb;

import fr.aytronn.moduloapi.api.Connector;
import fr.aytronn.moduloapi.mongodb.settings.MongoSettings;

/**
 * @author HookWoods
 */
public class MongoConnector extends Connector<MongoService> {

    private static final MongoConnector instance = new MongoConnector();

    /**
     * Used to create a new settings
     *
     * @param uri     the uri of the service
     *
     * @return the settings created
     */
    public MongoSettings createSettings(String uri) {
        return new MongoSettings(uri);
    }

    public static MongoConnector getInstance() {
        return instance;
    }
}
