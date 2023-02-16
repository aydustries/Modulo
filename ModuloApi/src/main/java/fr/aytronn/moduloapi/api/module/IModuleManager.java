package fr.aytronn.moduloapi.api.module;

import fr.aytronn.moduloapi.exceptions.module.InvalidModuleException;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.javacord.api.entity.Attachment;
import org.javacord.api.listener.GloballyAttachableListener;

import java.io.File;
import java.util.regex.Pattern;

public interface IModuleManager {

    /**
     * Useful to load every modules from the default modules folder
     *
     * @return the loaded modules
     */
    ObjectList<IModule> loadModules();

    /**
     * Useful to load every modules from a directory
     *
     * @param directory to load modules from
     *
     * @return the loaded module
     */
    ObjectList<IModule> loadModules(File directory);

    /**
     * Useful to enable a specific jar file
     *
     * @param file of the jarfile to load
     *
     * @return the loaded module
     *
     * @throws InvalidModuleException if the modules.yml is empty or corrupted
     */
    IModule loadModule(File file) throws InvalidModuleException, InvalidModuleException;

    /**
     * Get a module by it's name
     *
     * @param module name to get
     *
     * @return the module
     */
    IModule getModule(String module);

    /**
     * Useful to get every enabled modules
     *
     * @return enabled module list
     */
    ObjectList<IModule> getEnabledModule();

    /**
     * Useful to get every modules (loaded, disabled, enabled)
     *
     * @return every module
     */
    Object2ObjectMap<String, IModule> getModules();

    /**
     * Useful to define a class by it's name
     * in the class list
     *
     * @param name of the class
     *
     * @param clazz to be defined
     */
    void setClass(final String name, final Class<?> clazz);

    /**
     * Useful to get a class by it's name
     *
     * @param name of the class to get
     *
     * @return the class if defined
     */
    Class<?> getClassByName(final String name);

    /**
     * Useful to register a listener in the module
     * and to be disabled at anytime
     *
     * @param module of the listener
     *
     * @param listener to register
     */
    void registerListener(IModule module, GloballyAttachableListener listener);

    /**
     * Allow to register a command
     *
     * @param module IModule
     *
     * @param commandClass Command class
     */
    void registerCommand(IModule module, Object commandClass);

    /**
     * Allow to register an action
     *
     * @param module IModule
     *
     * @param actionClass Action class
     */
    void registerAction(IModule module, Object actionClass);

    /**
     * Useful to avoid memory leak and close
     * the custom class loader
     *
     * @param module to get
     *
     * @return the class loader
     */
    IModuleClassLoader getLoader(final IModule module);

    /**
     * Useful to get where module are placed
     *
     * @return the file dir of modules
     */
    File getModuleDir();

    /**
     * Useful to get a regex of jar pattern
     *
     * @return the regex pattern of jar file
     */
    Pattern getJarPattern();

    /**
     * Useful to get every loaders
     *
     * @return every module with their class loader
     */
    Object2ObjectMap<IModule, IModuleClassLoader> getLoaders();

    /**
     * Useful to get every listeners
     *
     * @return every module with their listeners
     */
    Object2ObjectMap<IModule, ObjectList<GloballyAttachableListener>> getListeners();

    /**
     * Useful to get every commands
     *
     * @return every module with their commands
     */
    Object2ObjectMap<IModule, ObjectList<Object>> getCommands();

    /**
     * Useful to get every class
     *
     * @return every class name with their class
     */
    Object2ObjectMap<String, Class<?>> getClasses();

    /**
     * Useful to get every actions
     *
     * @return every module with their actions
     */
    Object2ObjectMap<IModule, ObjectList<Object>> getActions();

    /**
     * Useful to get a module by it's class
     *
     * @param clazz of the module
     *
     * @return the module
     */
    IModule getModuleFromClass(Class<?> clazz);

    /**
     * Useful to reload every modules
     */
    void reloadModules();

    /**
     * Reload specific module
     *
     * @param module to be reloaded
     */
    void reloadModule(IModule module);

    /**
     * Useful to disable every modules
     */
    void disableModules();

    /**
     * Useful to disable a specific module
     *
     * @param module to disable
     *
     * @return true if the module was loaded
     */
    boolean loadModule(String module);

    /**
     * Useful to disable a specific module
     *
     * @param module to disable
     */
    void disableModule(IModule module);


    /**
     * Useful to download a module from an attachment
     *
     * @param attachment to download
     */
    void downloadModuleFromAttachment(Attachment attachment);

    /**
     * Useful to delete a module
     *
     * @param module to delete
     *
     * @return true if the module was deleted
     */
    boolean deleteModule(IModule module);
}
