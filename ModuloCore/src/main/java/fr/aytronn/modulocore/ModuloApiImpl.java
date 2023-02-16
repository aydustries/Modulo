package fr.aytronn.modulocore;

import com.google.gson.Gson;
import fr.aytronn.moduloapi.ModuloApi;
import fr.aytronn.moduloapi.api.action.IActionManager;
import fr.aytronn.moduloapi.api.command.ICommandManager;
import fr.aytronn.moduloapi.api.config.IPersist;
import fr.aytronn.moduloapi.api.module.IModuleManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.slf4j.Logger;

public class ModuloApiImpl extends ModuloApi {

    private final ModuloCore moduloCore;

    public ModuloApiImpl(ModuloCore moduloCore) {
        this.moduloCore = moduloCore;
    }

    /**
     * Useful to get the logger of the plugin
     *
     * @return the logger of the plugin
     */
    @Override
    public Logger getLogger() {
        return getModuloCore().getLogger();
    }


    /**
     * Useful to get the DiscordApi
     *
     * @return the DiscordApi
     */
    @Override
    public DiscordApi getDiscordApi() {
        return getModuloCore().getDiscordApi();
    }

    /**
     * Useful to get the Gson instance
     *
     * @return the Gson instance
     */
    @Override
    public Gson getGson() {
        return getModuloCore().getGson();
    }

    /**
     * Useful to get the module manager
     *
     * @return the module manager
     */
    @Override
    public IModuleManager getModuleManager() {
        return getModuloCore().getModuleManager();
    }

    /**
     * Useful to get the server discord
     *
     * @return the server discord
     */
    @Override
    public Server getDiscordServer() {
        return getModuloCore().getDiscordServer();
    }

    /**
     * Useful to get the persist object
     *
     * @return the persist object
     */
    @Override
    public IPersist getPersist() {
        return getModuloCore().getPersist();
    }

    /**
     * Useful to get the command manager
     *
     * @return the command manager
     */
    @Override
    public ICommandManager getCommandManager() {
        return getModuloCore().getCommandManager();
    }

    /**
     * Useful to get the action manager
     *
     * @return the action manager
     */
    @Override
    public IActionManager getActionManager() {
        return getModuloCore().getActionManager();
    }

    public ModuloCore getModuloCore() {
        return this.moduloCore;
    }
}
