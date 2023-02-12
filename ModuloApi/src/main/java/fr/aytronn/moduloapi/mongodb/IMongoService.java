package fr.aytronn.moduloapi.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * @author HookWoods
 */
public interface IMongoService {

    /**
     * Useful to communicate to the database
     *
     * @return the client connected to the database or null if not connected
     */
    public MongoClient getMongoClient();

    /**
     * Gets a specific database instance
     *
     * @param name the name of the database
     *
     * @return the database
     */
    public MongoDatabase getDatabase(String name);

    /**
     * Get Zakary database
     *
     * @return Zakary database
     */
    public MongoDatabase getDatabase();

    /**
     * Used to remove database connection
     */
    public void remove();

    /**
     * Used to check if the service is connected
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected();
}
