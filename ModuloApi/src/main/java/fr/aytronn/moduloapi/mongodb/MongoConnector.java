package fr.aytronn.moduloapi.mongodb;

import fr.aytronn.moduloapi.api.Connector;
import fr.aytronn.moduloapi.mongodb.settings.MongoSettings;

public class MongoConnector extends Connector<MongoService> {

    private static final MongoConnector instance = new MongoConnector();

    public MongoSettings createSettings(String uri) {
        return new MongoSettings(uri);
    }

    public static MongoConnector getInstance() {
        return instance;
    }
}