package fr.aytronn.modulocore;

import com.google.gson.Gson;
import fr.aytronn.moduloapi.ModuloApi;
import fr.aytronn.moduloapi.api.action.IActionManager;
import fr.aytronn.moduloapi.api.command.ICommandManager;
import fr.aytronn.moduloapi.api.config.IPersist;
import fr.aytronn.moduloapi.api.config.ISettingsManager;
import fr.aytronn.moduloapi.api.module.IModuleManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;

public class ModuloApiImpl extends ModuloApi {

    private final ModuloCore moduloCore;

    public ModuloApiImpl(ModuloCore moduloCore) {
        this.moduloCore = moduloCore;
    }

    @Override
    public Logger getLogger() {
        return getModuloCore().getLogger();
    }

    @Override
    public DiscordApi getDiscordApi() {
        return getModuloCore().getDiscordApi();
    }

    @Override
    public Gson getGson() {
        return getModuloCore().getGson();
    }

    @Override
    public IModuleManager getModuleManager() {
        return getModuloCore().getModuleManager();
    }

    @Override
    public Server getDiscordServer() {
        return getModuloCore().getDiscordServer();
    }

    @Override
    public IPersist getPersist() {
        return getModuloCore().getPersist();
    }

    @Override
    public ICommandManager getCommandManager() {
        return getModuloCore().getCommandManager();
    }

    @Override
    public IActionManager getActionManager() {
        return getModuloCore().getActionManager();
    }

    @Override
    public ISettingsManager getSettingsManager() {
        return getModuloCore().getSettingsManager();
    }

    public ModuloCore getModuloCore() {
        return this.moduloCore;
    }
}
