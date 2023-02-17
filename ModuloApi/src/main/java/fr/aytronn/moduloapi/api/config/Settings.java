package fr.aytronn.moduloapi.api.config;

import com.mongodb.BasicDBObject;
import fr.aytronn.moduloapi.ModuloApi;
import org.bson.Document;

public class Settings {

    private long channelUpdater;

    public Settings() {
    }

    public long getChannelUpdater() {
        return this.channelUpdater;
    }

    public void setChannelUpdater(long channelUpdater) {
        this.channelUpdater = channelUpdater;
    }

    public static Settings fromDocument(Document document) {
        if (document == null) return null;
        return ModuloApi.getInstance().getGson().fromJson(BasicDBObject.parse(document.toJson()).toString(), Settings.class);
    }

    public Document toDocument() {
        return Document.parse(ModuloApi.getInstance().getGson().toJson(this));
    }
}
