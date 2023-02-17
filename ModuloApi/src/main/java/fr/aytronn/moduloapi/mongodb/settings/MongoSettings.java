package fr.aytronn.moduloapi.mongodb.settings;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import fr.aytronn.moduloapi.api.mongo.Settings;

/**
 * This class is useful to manage the settings of the database
 *
 * @param uri the uri of the database
 *
 * @author HookWoods
 */
public record MongoSettings(String uri) implements Settings {

    @Override
    public MongoClient toFactory() {
        try {
            return MongoClients.create(uri());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
