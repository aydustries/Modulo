package fr.aytronn.moduloapi.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import fr.aytronn.moduloapi.ModuloApi;
import fr.aytronn.moduloapi.api.AutoReconnector;
import fr.aytronn.moduloapi.mongodb.settings.MongoSettings;

public class MongoService extends AutoReconnector implements IMongoService {

    private MongoClient mongoClient;

    public MongoService(String name, MongoSettings settings) {
        super(name, settings);
        this.reconnect();
    }

    @Override
    public void remove() {
        if (isDead()) {
            ModuloApi.getInstance().getLogger().warn("DatabaseAPI - MongoDB: The service is already dead.");
            return;
        }
        final long time = System.currentTimeMillis();
        setDead();
        getTask().cancel(); // Cancel AutoReconnector task
        // Close channel
        try {
            getMongoClient().close();
            this.mongoClient = null;
        } catch (Exception error) {
            ModuloApi.getInstance().getLogger().warn("DatabaseAPI - MongoDB: Something gone wrong while trying to close connection.");
            error.printStackTrace();
            return;
        }
        MongoConnector.getInstance().getServices().remove(this.getName());
        ModuloApi.getInstance().getLogger().info("DatabaseAPI - MongoDB: Service disconnected ({} ms).", System.currentTimeMillis() - time);
    }

    @Override
    public boolean isConnected() {
        if (getMongoClient() == null) return false;
        try {
            getMongoClient().getDatabase("admin");
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public void reconnect() {
        if (isDead() || isConnected()) {
            return;
        }
        try {
            final long time = System.currentTimeMillis();
            this.mongoClient = (MongoClient) getSettings().toFactory();
            ModuloApi.getInstance().getLogger().info("DatabaseAPI - MongoDB: Successfully (re)connected to the service ({} ms).", System.currentTimeMillis() - time);
        } catch (Exception error) {
            error.printStackTrace();
            ModuloApi.getInstance().getLogger().warn("DatabaseAPI - MongoDB: Unable to connect to the service ({}).", error.getMessage());
        }
    }

    @Override
    public MongoDatabase getDatabase(String name) {
        return getMongoClient().getDatabase(name);
    }

    @Override
    public MongoDatabase getDatabase() {
        return getDatabase("Zakary");
    }

    @Override
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

}
