package fr.aytronn.modulocore.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import fr.aytronn.moduloapi.ModuloApi;
import fr.aytronn.moduloapi.api.config.ISettingsManager;
import fr.aytronn.moduloapi.api.config.Settings;
import fr.aytronn.modulocore.ModuloCore;
import org.bson.Document;
import org.bson.conversions.Bson;

public class SettingsManager implements ISettingsManager {
    private Settings settings;

    public SettingsManager() {
        switch (ModuloCore.getInstance().getConfig().getSaveType()) {
            case MONGODB -> getSettingsMongoDb();
            default -> getSettingsFile();
        }
    }

    private void getSettingsMongoDb() {
        final MongoCollection<Document> collection = ModuloApi.getInstance().getMongoService().getDatabase().getCollection("settings");
        final Bson filter = Filters.and(
                Filters.eq("key", "Modulo_settings"),
                Filters.eq("serverId", ModuloCore.getInstance().getConfig().getServerId())
        );
        try (MongoCursor<Document> mongoCursor = collection.find(filter).limit(1).iterator()) {
            if (mongoCursor.hasNext()) {
                final var document = mongoCursor.next();
                this.settings = Settings.fromDocument(document.get("settings", Document.class));
            } else {
                final Document document = new Document();
                this.settings = new Settings();

                document.append("key", "Modulo_settings");
                document.append("serverId", ModuloCore.getInstance().getConfig().getServerId());
                document.append("settings", this.settings.toDocument());

                collection.insertOne(document);
            }
        }
    }

    private void getSettingsFile() {
        if (ModuloCore.getInstance().getPersist().getFile(Settings.class).exists()) {
            this.settings = ModuloCore.getInstance().getPersist().load(Settings.class);
        } else {
            this.settings = new Settings();
            ModuloCore.getInstance().getPersist().save(getSettings());
        }
    }

    @Override
    public void saveSettings() {
        switch (ModuloCore.getInstance().getConfig().getSaveType()) {
            case MONGODB -> saveSettingsMongoDb();
            default -> saveSettingsFile();
        }
    }

    private void saveSettingsFile() {
        ModuloCore.getInstance().getPersist().save(getSettings());
    }

    private void saveSettingsMongoDb() {
        final MongoCollection<Document> collection = ModuloApi.getInstance().getMongoService().getDatabase().getCollection("settings");
        final Bson filter = Filters.and(
                Filters.eq("key", "Modulo_settings"),
                Filters.eq("serverId", ModuloCore.getInstance().getConfig().getServerId())
        );
        try (MongoCursor<Document> mongoCursor = collection.find(filter).limit(1).iterator()) {
            if (mongoCursor.hasNext()) {
                final var document = mongoCursor.next();
                document.put("settings", getSettings().toDocument());
                collection.replaceOne(filter, document);
            } else {
                final Document document = new Document();

                if (this.settings == null) this.settings = new Settings();

                document.append("key", "Modulo_settings");
                document.append("serverId", ModuloCore.getInstance().getConfig().getServerId());
                document.append("settings", this.settings.toDocument());

                collection.insertOne(document);
            }
        }
    }

    @Override
    public Settings getSettings() {
        return this.settings;
    }
}
