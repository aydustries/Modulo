package fr.aytronn.modulocore;

import com.google.gson.Gson;
import fr.aytronn.moduloapi.ModuloApi;
import fr.aytronn.moduloapi.modules.IModuleManager;
import org.javacord.api.DiscordApi;
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

    public ModuloCore getModuloCore() {
        return this.moduloCore;
    }
}
