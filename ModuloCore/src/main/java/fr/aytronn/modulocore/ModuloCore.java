package fr.aytronn.modulocore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.aytronn.moduloapi.api.config.Configuration;
import fr.aytronn.moduloapi.exceptions.ServerNotFoundException;
import fr.aytronn.moduloapi.utils.threads.ZakaryThread;
import fr.aytronn.modulocore.actions.CoreAction;
import fr.aytronn.modulocore.commands.CoreCommand;
import fr.aytronn.modulocore.config.Persist;
import fr.aytronn.modulocore.listeners.ActionListener;
import fr.aytronn.modulocore.listeners.ChannelUpdaterListener;
import fr.aytronn.modulocore.listeners.CommandListener;
import fr.aytronn.modulocore.managers.ActionManager;
import fr.aytronn.modulocore.managers.CommandManager;
import fr.aytronn.modulocore.managers.SettingsManager;
import fr.aytronn.modulocore.managers.module.ModuleManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Scanner;

public class ModuloCore {
    private static ModuloCore instance;
    private final Logger logger;
    private final DiscordApi discordApi;
    private final Persist persist;
    private final Gson gson;
    private Configuration config;
    private File folder;
    private final CommandManager commandManager;
    private final SettingsManager settingsManager;
    private final ActionManager actionManager;
    private final ModuloApiImpl moduloApi;
    private final Server discordServer;
    private final ModuleManager moduleManager;

    public ModuloCore(String[] args) throws Exception {
        instance = this;
        this.logger = LoggerFactory.getLogger(ModuloCore.class);
        getLogger().info("==========- " + "ModuloAPI" + " -==========");
        long startMillis = System.currentTimeMillis();
        final long initMillis = startMillis;
        getLogger().info("Loading configuration");
        this.gson = new GsonBuilder().setPrettyPrinting().setLenient().disableHtmlEscaping().enableComplexMapKeySerialization().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE).create();
        this.persist = new Persist();
        loadConfiguration();
        getLogger().info("Configuration loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");
        startMillis = System.currentTimeMillis();
        getLogger().info("Loading api");
        this.moduloApi = new ModuloApiImpl(getInstance());
        getModuloApi().setupMongo(getConfig().getMongoUri());
        this.settingsManager = new SettingsManager();
        getLogger().info("Databases loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading DiscordApi");
        this.discordApi = new DiscordApiBuilder()
                .setToken(getConfig().getBotToken())
                .setAllIntents()
                .login()
                .join();

        final Optional<Server> serverById = getDiscordApi().getServerById(getConfig().getServerId());

        if (serverById.isPresent()) {
            this.discordServer = serverById.get();
            getLogger().info("Server found");
        } else {
            throw new ServerNotFoundException("Server not found");
        }

        FallbackLoggerConfiguration.setDebug(true);
        FallbackLoggerConfiguration.setTrace(true);

        getLogger().info("DiscordApi loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading Modules");
        this.commandManager = new CommandManager();
        this.actionManager = new ActionManager();
        registerCommands();
        registerActions();
        this.moduleManager = new ModuleManager();
        getModuleManager().loadModules();
        getLogger().info("Modules loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Loading utils");
        registerListeners();
        getCommandManager().loadCommands();
        getLogger().info("Utils loaded (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        getLogger().info("ModuloAPI loaded (" + (System.currentTimeMillis() - initMillis) + ") ms.");
    }

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        new Thread(() -> {
            final String input = scanner.nextLine();
            if (input.equals("stop")) {
                ModuloCore.getInstance().stop();
            }
        }).start();
        try {
            new ModuloCore(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        long startMillis = System.currentTimeMillis();
        getLogger().info("Shutting down...");
        getLogger().info("Stopping Modules manager");
        getModuleManager().disableModules();
        while (!getModuleManager().getEnabledModule().isEmpty()) ;
        getLogger().info("Modules manager stopped (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        startMillis = System.currentTimeMillis();
        getLogger().info("Unregistering commands");
        getCommandManager().unregisterAllCommand();
        getLogger().info("Commands unregistered (" + (System.currentTimeMillis() - startMillis) + ") ms.");

        if (getModuloApi().getMongoService() != null) {
            startMillis = System.currentTimeMillis();
            getLogger().info("Stopping MongoDB");
            getModuloApi().getMongoService().remove();
            while (getModuloApi().getMongoService().isConnected()) ;
            getLogger().info("MongoDB stopped (" + (System.currentTimeMillis() - startMillis) + ") ms.");
        }

        getLogger().info("Stopping Modulo Thread");
        ZakaryThread.shutdownAll();
        getDiscordApi().disconnect();
        getLogger().info("Zakary Thread stopped.");
        getLogger().info("ModuloCore stopped");
        System.exit(0);
    }

    public void loadConfiguration() {
        if (getPersist().getFile(Configuration.class).exists()) {
            this.config = getPersist().load(Configuration.class);
        } else {
            this.config = new Configuration();
            getPersist().save(getConfig());
        }
    }

    private void registerListeners() {
        getDiscordApi().addListener(new CommandListener());
        getDiscordApi().addListener(new ActionListener());
        getDiscordApi().addListener(new ChannelUpdaterListener());
    }

    private void registerCommands() {
        getCommandManager().registerCommand(new CoreCommand());
    }

    private void registerActions() {
        getActionManager().registerAction(new CoreAction());
    }

    public static ModuloCore getInstance() {
        return instance;
    }

    public Configuration getConfig() {
        return this.config;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public DiscordApi getDiscordApi() {
        return this.discordApi;
    }

    public File getDataFolder() {
        if (this.folder == null) {
            this.folder = new File("ModuloCore");
            if (!this.folder.exists()) {
                this.folder.mkdirs();
            }
        }
        return this.folder;
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

    public Server getDiscordServer() {
        return this.discordServer;
    }

    public ActionManager getActionManager() {
        return this.actionManager;
    }

    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }
}
