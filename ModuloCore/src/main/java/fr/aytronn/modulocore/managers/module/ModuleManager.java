package fr.aytronn.modulocore.managers.module;

import fr.aytronn.moduloapi.modules.IModule;
import fr.aytronn.moduloapi.modules.IModuleClassLoader;
import fr.aytronn.moduloapi.modules.IModuleManager;
import fr.aytronn.moduloapi.modules.exception.InvalidModuleException;
import fr.aytronn.moduloapi.utils.threads.ZakaryThread;
import fr.aytronn.modulocore.ModuloCore;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.javacord.api.listener.GloballyAttachableListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ModuleManager implements IModuleManager {

    private final Pattern jarPattern;
    private final File moduleDir;
    private final Object2ObjectMap<String, IModule> modules;
    private final Object2ObjectMap<IModule, IModuleClassLoader> loaders;
    private final Object2ObjectMap<IModule, ObjectList<GloballyAttachableListener>> listeners;
    private final Object2ObjectMap<IModule, ObjectList<Object>> commands;
    private final Object2ObjectMap<String, Class<?>> classes;

    public ModuleManager() {
        this.jarPattern = Pattern.compile("(.+?)(\\.jar)");
        this.moduleDir = new File(ModuloCore.getInstance().getDataFolder(), "modules");
        this.modules = new Object2ObjectOpenHashMap<>();
        this.listeners = new Object2ObjectOpenHashMap<>();
        this.commands = new Object2ObjectOpenHashMap<>();
        this.loaders = new Object2ObjectOpenHashMap<>();
        this.classes = new Object2ObjectOpenHashMap<>();

        if (!this.moduleDir.isDirectory()) {
            this.moduleDir.delete();
        }

        if (!this.moduleDir.exists()) {
            this.moduleDir.mkdirs();
        }
    }

    /**
     * Useful to load every modules from the default modules folder
     *
     * @return the loaded modules
     */
    @Override
    public ObjectList<IModule> loadModules() {
        try {
            return this.loadModules(getModuleDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ObjectArrayList<>();
    }

    /**
     * Useful to load every modules from a directory
     *
     * @param directory to load modules from
     * @return the loaded module
     */
    @Override
    public ObjectList<IModule> loadModules(File directory) {
        final CompletableFuture<ObjectList<File>> fileCompletable = CompletableFuture.supplyAsync(() -> {
            final ObjectList<File> files = new ObjectArrayList<>();

            for (final File file : directory.listFiles()) {
                if (getJarPattern().matcher(file.getName()).matches()) {
                    files.add(file);
                }

            }

            return files;
        }, ZakaryThread.FILE_EXECUTOR.get());

        final ObjectList<IModule> result = new ObjectArrayList<>();
        try {
            for (final File file : fileCompletable.get()) {
                try {
                    final IModule module = loadModule(file);
                    if (module != null) {
                        result.add(module);
                    }
                } catch (Exception | InvalidModuleException e) {
                    ModuloCore.getInstance().getLogger().warn(
                            "ModuloAPI - Modules: Cannot load '"
                                    + file.getName()
                                    + "' in folder '"
                                    + directory.getPath()
                                    + "': " + e.getMessage());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Useful to enable a specific jar file
     *
     * @param file of the jarfile to load
     * @return the loaded module
     */
    @Override
    public IModule loadModule(File file) throws InvalidModuleException {
        final IModule result;

        if (!file.exists()) throw new InvalidModuleException(new FileNotFoundException("ModuloAPI - Modules: " + file.getPath() + " does not exist"));

        final var dataFolder = new File(getModuleDir() + File.separator + file.getName());

        final Runnable runnable = () -> {
            if (!dataFolder.exists())
                dataFolder.mkdir();
        };
        ZakaryThread.FILE_EXECUTOR.execute(runnable);

        final ModuleClassLoader loader;

        try {
            // We don't close this instance since we need it to find the classes of any modules in runtime
            loader = new ModuleClassLoader(getClass().getClassLoader(), file, dataFolder, this);
            result = loader.getModule();
        } catch (Exception e) {
            throw new InvalidModuleException(e);
        }

        if (result != null) {
            getModules().put(result.getModuleInfo().getName(), result);
            ModuloCore.getInstance().getLogger().info(
                    "ModuloAPI - Modules: "
                            + result.getModuleInfo().getName()
                            + " [v."
                            + result.getModuleInfo().getVersion()
                            + " by "
                            + result.getModuleInfo().getAuthorsInLine()
                            + "] loaded");

            getLoaders().put(result, loader);

        }

        return result;
    }



    /**
     * Get a module by it's name
     *
     * @param module name to get
     * @return the module
     */
    @Override
    public IModule getModule(String module) {
        return getModules().get(module);
    }


    /**
     * Useful to get every enabled modules
     *
     * @return enabled module list
     */
    @Override
    public ObjectList<IModule> getEnabledModule() {
        final ObjectList<IModule> list = new ObjectArrayList<>();
        for (final IModule module : getModules().values()) {
            if (module.getState().equals(IModule.State.ENABLED)) {
                list.add(module);
            }
        }
        return list;
    }

    /**
     * Useful to get every modules (loaded, disabled, enabled)
     *
     * @return every module
     */
    @Override
    public Object2ObjectMap<String, IModule> getModules() {
        return this.modules;
    }

    /**
     * Useful to define a class by it's name
     * in the class list
     *
     * @param name  of the class
     * @param clazz to be defined
     */
    @Override
    public void setClass(String name, Class<?> clazz) {
        getClasses().putIfAbsent(name, clazz);
    }

    /**
     * Useful to get a class by it's name
     *
     * @param name of the class to get
     * @return the class if defined
     */
    @Override
    public Class<?> getClassByName(String name) {
        try {
            for (final Map.Entry<IModule, IModuleClassLoader> classes : getLoaders().entrySet()) {
                final Class<?> iter = classes.getValue().findClass(name, false);
                if (iter != null) {
                    return iter;
                }
            }
            return null;
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Useful to register a listener in the module
     * and to be disabled at anytime
     *
     * @param module   of the listener
     * @param listener to register
     */
    @Override
    public void registerListener(IModule module, GloballyAttachableListener listener) {
        ModuloCore.getInstance().registerListener(listener);
        getListeners().computeIfAbsent(module, k -> new ObjectArrayList<>()).add(listener);

    }

    /**
     * Allow to register a command
     *
     * @param module       IModule
     * @param commandClass Command class
     */
    @Override
    public void registerCommand(IModule module, Object commandClass) {
        ModuloCore.getInstance().registerCommand(commandClass);
        getCommands().computeIfAbsent(module, k -> new ObjectArrayList<>()).add(commandClass);
    }

    /**
     * Useful to avoid memory leak and close
     * the custom class loader
     *
     * @param module to get
     * @return the class loader
     */
    @Override
    public IModuleClassLoader getLoader(IModule module) {
        return this.loaders.get(module);
    }

    /**
     * Useful to get where module are placed
     *
     * @return the file dir of modules
     */
    @Override
    public File getModuleDir() {
        return this.moduleDir;
    }

    /**
     * Useful to get a regex of jar pattern
     *
     * @return the regex pattern of jar file
     */
    @Override
    public Pattern getJarPattern() {
        return this.jarPattern;
    }

    /**
     * Useful to get every loaders
     *
     * @return every module with their class loader
     */
    @Override
    public Object2ObjectMap<IModule, IModuleClassLoader> getLoaders() {
        return this.loaders;
    }

    /**
     * Useful to get every listeners
     *
     * @return every module with their listeners
     */
    @Override
    public Object2ObjectMap<IModule, ObjectList<GloballyAttachableListener>> getListeners() {
        return this.listeners;
    }

    /**
     * Useful to get every commands
     *
     * @return every module with their commands
     */
    @Override
    public Object2ObjectMap<IModule, ObjectList<Object>> getCommands() {
        return this.commands;
    }

    /**
     * Useful to get every class
     *
     * @return every class name with their class
     */
    @Override
    public Object2ObjectMap<String, Class<?>> getClasses() {
        return this.classes;
    }

    @Override
    public IModule getModuleFromClass(Class<?> clazz) {
        for (final Map.Entry<IModule, IModuleClassLoader> loaders : getLoaders().entrySet()) {
            for (final String loaderClazz : loaders.getValue().getClasses()) {
                if (loaderClazz.equalsIgnoreCase(clazz.getName())) {
                    return loaders.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public void reloadModules() {
        disableModules();
        loadModules();
    }

    @Override
    public void disableModules() {
        if (!getEnabledModule().isEmpty()) {
            getEnabledModule().forEach(this::disableModule);
            ModuloCore.getInstance().getLogger().info("ModuloAPI - Modules: Successfully disabled.");
        }
        getCommands().clear();
        getListeners().clear();
        getModules().clear();
        getLoaders().clear();
        getClasses().clear();
    }

    @Override
    public void disableModule(IModule module) {
        // Clear listeners
        if (getListeners().containsKey(module)) {
            getListeners().get(module).forEach(globallyAttachableListener -> {
                ModuloCore.getInstance().unregisterListener(globallyAttachableListener);
            });
            getListeners().remove(module);
        }
        if (getCommands().containsKey(module)) {
            getCommands().get(module).forEach(command -> {
                ModuloCore.getInstance().unregisterCommand(command);
            });
            getCommands().remove(module);
        }
        if (module.isEnabled()) {
            ModuloCore.getInstance().getLogger().info("ModuloAPI - Modules: Disabling " + module.getModuleInfo().getName() + "...");
            try {
                module.onDisable();
            } catch (Exception e) {
                ModuloCore.getInstance().getLogger().error("ModuloAPI - Modules: Error while disabling " + module.getModuleInfo().getName(), e);
            }
        }
        // Clear loaders
        if (getLoaders().containsKey(module)) {
            final ObjectSet<String> unmodifiableSet = getLoaders().get(module).getClasses();
            for (final String className : unmodifiableSet) {
                getClasses().remove(className);
            }
            module.setState(IModule.State.DISABLED);
            try {
                getLoaders().get(module).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            getLoaders().remove(module);
        }
        // Remove it from the addons list
        getModules().remove(module.getModuleInfo().getName());
    }
}
