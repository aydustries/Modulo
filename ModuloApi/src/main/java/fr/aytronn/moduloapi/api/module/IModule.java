package fr.aytronn.moduloapi.api.module;

import fr.aytronn.moduloapi.ModuloApi;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.listener.GloballyAttachableListener;
import org.slf4j.Logger;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author HookWoods
 */
public abstract class IModule {
    private IModuleInfo moduleInfo;
    private File dataFolder;
    private ClassLoader loader;
    private State state;
    private File file;
    private Logger logger;

    /**
     * IModule
     */
    public IModule() {
        this.state = State.DISABLED;
    }

    /**
     * ModuloAPI ONLY
     * Useful to set the current module info of the module
     *
     * @param moduleInfo of the module
     */
    public void setModuleInfo(IModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    /**
     * ModuloAPI ONLY
     * Useful to init the module
     *
     * @param classLoader of the module
     * @param dataFolder  of the module
     */
    public void init(IModuleClassLoader classLoader, File dataFolder) {
        try {
            setDataFolder(dataFolder);
            setLoader(classLoader);
            if (getLogger() == null) setLogger(ModuloApi.getInstance().getLogger());

            onEnable();
            setState(State.ENABLED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Allow to set the jar files
     *
     * @param file File
     */
    public void setJar(File file) {
        this.file = file;
    }

    /**
     * Allow to get the jar files
     *
     * @return File
     */
    public File getJarFile() {
        return this.file;
    }

    /**
     * Useful function when the module is enabled by the core
     */
    public abstract void onEnable();

    /**
     * Useful to set the data folder of the module
     */
    public abstract void onDisable();

    /**
     * Useful to log data to console
     *
     * @return the custom logger of the modules
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Useful to get access to Bukkit api
     *
     * @return the api server of the ModuloAPI
     */
    public DiscordApi getDiscordApi() {
        return ModuloApi.getInstance().getDiscordApi();
    }

    /**
     * Useful to save data from jar resource
     * to module folder
     *
     * @param jarResource       the jarfile
     * @param destinationFolder the destination folder to put the resource
     * @param replace           erase or not if the resource already existe
     * @param noPath            if no path is specified, set the name of the resource as file name
     *
     * @return the specific file that has been moved
     */
    public File saveResource(String jarResource, File destinationFolder, boolean replace, boolean noPath) {
        if (jarResource == null || jarResource.equals("")) {
            throw new IllegalArgumentException("ModuloAPI - Modules: ResourcePath cannot be null or empty");
        }

        jarResource = jarResource.replace( "\\", "/");
        try (JarFile jar = new JarFile(getJarFile())) {
            final JarEntry jarConfig = jar.getJarEntry(jarResource);
            if (jarConfig != null) {
                try (InputStream in = jar.getInputStream(jarConfig)) {
                    if (in == null) {
                        throw new IllegalArgumentException("ModuloAPI - Modules: The embedded resource '" + jarResource + "' cannot be found in " + jar.getName());
                    }
                    // There are two options, use the path of the resource or not
                    File outFile = new File(destinationFolder, jarResource);
                    if (noPath) {
                        outFile = new File(destinationFolder, outFile.getName());
                    }
                    // Make any dirs that need to be made
                    outFile.getParentFile().mkdirs();
                    if (!outFile.exists() || replace) {
                        Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    return outFile;
                }
            } else {
                // No file in the jar
                throw new IllegalArgumentException("ModuloAPI - Modules: The embedded resource '" + jarResource + "' cannot be found in " + jar.getName());
            }
        } catch (IOException e) {
            getLogger().error("ModuloAPI - Modules: Could not save from jar file. From " + jarResource + " to " + destinationFolder.getAbsolutePath());
        }
        return null;
    }

    /**
     * Useful to place a jar from the resource folder
     * to the modules datafolder
     *
     * @param resourcePath the jarfile
     * @param replace      if the file already exist or not
     */
    public void saveResource(String resourcePath, boolean replace) {
        saveResource(resourcePath, getDataFolder(), replace, false);
    }

    /**
     * Useful to get a resource from the jarfile
     *
     * @param jarResource the file to get
     *
     * @return return an InputStream of the file
     */
    public InputStream getResource(String jarResource) {
        if (jarResource == null || jarResource.equals("")) {
            throw new IllegalArgumentException("ModuloAPI - Modules: ResourcePath cannot be null or empty");
        }

        jarResource = jarResource.replace( "\\", "/");
        try (JarFile jar = new JarFile(getJarFile())) {
            final JarEntry jarConfig = jar.getJarEntry(jarResource);
            if (jarConfig != null) {
                try (InputStream in = jar.getInputStream(jarConfig)) {
                    return in;
                }
            }
        } catch (IOException e) {
            ModuloApi.getInstance().getLogger().error("ModuloAPI - Modules: Could not open from jar file. " + jarResource);
        }
        return null;
    }

    /**
     * Useful to directly access to the api
     *
     * @return the ModuloAPI instance
     */
    public ModuloApi getAPI() {
        return ModuloApi.getInstance();
    }

    /**
     * Useful to get info from this module
     *
     * @return the ModuleInfo class of this module
     */
    public IModuleInfo getModuleInfo() {
        return this.moduleInfo;
    }

    /**
     * Useful to get data folder of this module
     *
     * @return the file folder of this module folder
     */
    public File getDataFolder() {
        return this.dataFolder;
    }

    /**
     * Useful to get the specific class loader of the module
     *
     * @return the custom class loader of this module
     */
    public ClassLoader getLoader() {
        return this.loader;
    }

    /**
     * Useful to get if the state of the module is enabled or not
     *
     * @return if the module is enabled or not
     */
    public boolean isEnabled() {
        return getState() == State.ENABLED;
    }

    /**
     * Useful to register a listener to the module
     *
     * @param listener to register to the module
     */
    public void registerListener(GloballyAttachableListener listener) {
        ModuloApi.getInstance().getModuleManager().registerListener(this, listener);
    }

    /**
     * Useful to register a command to the module
     *
     * @param commandClass to register to the module
     */
    public void registerCommand(Object commandClass) {
        ModuloApi.getInstance().getModuleManager().registerCommand(this, commandClass);
    }

    public void registerAction(Object commandClass) {
        ModuloApi.getInstance().getModuleManager().registerAction(this, commandClass);
    }

    /**
     * ModuloAPI ONLY
     * Useful to set the current data folder of the module
     *
     * @param dataFolder of the folder
     */
    void setDataFolder(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    /**
     * ModuloAPI ONLY
     * Useful to set the current class loader of the module
     *
     * @param loader of the class loader
     */
    void setLoader(IModuleClassLoader loader) {
        this.loader = loader.getClassLoader();
    }

    /**
     * ModuloAPI ONLY
     * Useful to set the current state of the module
     *
     * @param state of the class loader
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * ModuloAPI ONLY
     * Useful to set the current logger of the module
     *
     * @param logger of the logger
     */
    void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Useful to get the state of the module
     *
     * @return the current state of the module
     */
    public State getState() {
        return this.state;
    }

    /**
     * Useful to get the embed of the module
     *
     * @return the current embed of the module
     */
    public EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle("Module " + getModuleInfo().getName())
                .setDescription(getModuleInfo().getDescription())
                .setColor(getState().equals(State.ENABLED) ? Color.GREEN : Color.RED)
                .setAuthor(getModuleInfo().getAuthorsInLine())
                .addField("State:", getState().name())
                .setFooter("ModuloAPI - Modules");
    }

    public enum State {
        /**
         * The addon has been correctly loaded.
         */
        LOADED,

        /**
         * The addon has been correctly enabled and is now fully working.
         */
        ENABLED,

        /**
         * The addon is fully disabled.
         */
        DISABLED
    }

}
