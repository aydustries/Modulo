package fr.aytronn.modulocore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.aytronn.moduloapi.config.Configuration;
import fr.aytronn.modulocore.config.Persist;
import fr.aytronn.modulocore.listeners.CommandListener;
import fr.aytronn.modulocore.managers.CommandManager;
import fr.aytronn.modulocore.managers.module.ModuleManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.GloballyAttachableListener;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Modifier;

public class ModuloCore {
    private static ModuloCore instance;
    private final Logger log;
    private final DiscordApi api;
    private Persist persist;
    private final Gson gson;
    private Configuration config;
    private final CommandManager commandManager;
    private final ModuloApiImpl moduloApi;

    private final ModuleManager moduleManager;

    public ModuloCore(String[] args) {
        instance = this;
        this.log = LoggerFactory.getLogger(ModuloCore.class);
        getLogger().info("==========- " + "ModuloCore" + " -==========");
        long startMillis = System.currentTimeMillis();
        getLogger().info("Loading configuration");
        this.gson = new GsonBuilder().setPrettyPrinting().setLenient().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).create();
        this.persist = new Persist();
        loadConfiguration();
        getLogger().info("Configuration loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading api");
        this.moduloApi = new ModuloApiImpl(getInstance());
        getModuloApi().setupMongo(getConfig().getMongoUri());
        getLogger().info("Databases loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading DiscordApi");
        this.api = new DiscordApiBuilder()
                .setToken(getConfig().getBotToken())
                .setAllIntents()
                .login()
                .join();

        FallbackLoggerConfiguration.setDebug(true);
        FallbackLoggerConfiguration.setTrace(true);
        getLogger().info("DiscordApi loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading Modules");
        this.commandManager = new CommandManager();
        this.moduleManager = new ModuleManager();
        try {
            getModuleManager().loadModules();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getLogger().info("Modules loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading utils");
        registerListeners();
        getCommandManager().loadCommands();
        getLogger().info("Utils loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");
    }

    public static void main(String[] args) {
        try {
            new ModuloCore(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfiguration() {
        if (getPersist().getFile(Configuration.class).exists()) {
            this.config = getPersist().load(Configuration.class);
        } else {
            this.config = new Configuration();
            getPersist().save(getConfig());
        }
    }

    public void registerCommands(Object object) {
        getCommandManager().registerCommand(object);
    }

    public void registerListeners(GloballyAttachableListener listener) {
        getDiscordApi().addListener(listener);
    }

    private void registerListeners() {
        registerListeners(new CommandListener());
    }

    public static ModuloCore getInstance() {
        return instance;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public Logger getLogger() {
        return this.log;
    }

    public DiscordApi getDiscordApi() {
        return this.api;
    }

    public File getDataFolder() {
        return new File("ModuloCore");
    }

    public Gson getGson() {
        return this.gson;
    }

    public Persist getPersist() {
        return this.persist;
    }

    public ModuloApiImpl getModuloApi() {
        return this.moduloApi;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }
}
