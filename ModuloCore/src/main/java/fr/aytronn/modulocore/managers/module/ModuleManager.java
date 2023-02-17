package fr.aytronn.modulocore.managers.module;

import fr.aytronn.moduloapi.api.module.IModule;
import fr.aytronn.moduloapi.api.module.IModuleClassLoader;
import fr.aytronn.moduloapi.api.module.IModuleManager;
import fr.aytronn.moduloapi.exceptions.module.InvalidModuleException;
import fr.aytronn.moduloapi.utils.threads.ZakaryThread;
import fr.aytronn.modulocore.ModuloCore;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.javacord.api.entity.Attachment;
import org.javacord.api.listener.GloballyAttachableListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * @author HookWoods
 */
public class ModuleManager implements IModuleManager {

    private final Pattern jarPattern;
    private final File moduleDir;
    private final Object2ObjectMap<String, IModule> modules;
    private final Object2ObjectMap<IModule, IModuleClassLoader> loaders;
    private final Object2ObjectMap<IModule, ObjectList<GloballyAttachableListener>> listeners;
    private final Object2ObjectMap<IModule, ObjectList<Object>> commands;
    private final Object2ObjectMap<IModule, ObjectList<Object>> actions;
    private final Object2ObjectMap<String, Class<?>> classes;

    public ModuleManager() {
        this.jarPattern = Pattern.compile("(.+?)(\\.jar)");
        this.moduleDir = new File(ModuloCore.getInstance().getDataFolder(), "modules");
        this.modules = new Object2ObjectOpenHashMap<>();
        this.listeners = new Object2ObjectOpenHashMap<>();
        this.commands = new Object2ObjectOpenHashMap<>();
        this.actions = new Object2ObjectOpenHashMap<>();
        this.loaders = new Object2ObjectOpenHashMap<>();
        this.classes = new Object2ObjectOpenHashMap<>();

        if (!this.moduleDir.isDirectory()) {
            this.moduleDir.delete();
        }

        if (!this.moduleDir.exists()) {
            this.moduleDir.mkdirs();
        }
    }

    @Override
    public ObjectList<IModule> loadModules() {
        try {
            return this.loadModules(getModuleDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ObjectArrayList<>();
    }

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
                    ModuloCore.getInstance().getLogger().warn("ModuloAPI - Modules: Cannot load '" + file.getName()
                            + "' in folder '" + directory.getPath() + "': " + e.getMessage());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public IModule loadModule(File file) throws InvalidModuleException {
        final IModule result;

        if (!file.exists()) throw new InvalidModuleException(new FileNotFoundException("ModuloAPI - Modules: " + file.getPath() + " does not exist"));

        final var dataFolder = new File(getModuleDir() + File.separator + file.getName());

        final Runnable runnable = () -> {
            if (!dataFolder.exists()) dataFolder.mkdir();
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
            ModuloCore.getInstance().getLogger().info("ModuloAPI - Modules: " + result.getModuleInfo().getName()
                    + " [v." + result.getModuleInfo().getVersion() + " by " + result.getModuleInfo().getAuthorsInLine() + "] loaded");

            getLoaders().put(result, loader);

        }

        return result;
    }

    @Override
    public IModule getModule(String module) {
        return getModules().get(module);
    }

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

    @Override
    public Object2ObjectMap<String, IModule> getModules() {
        return this.modules;
    }

    @Override
    public void setClass(String name, Class<?> clazz) {
        getClasses().putIfAbsent(name, clazz);
    }

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

    @Override
    public void registerListener(IModule module, GloballyAttachableListener listener) {
        ModuloCore.getInstance().getDiscordApi().addListener(listener);
        getListeners().computeIfAbsent(module, k -> new ObjectArrayList<>()).add(listener);

    }

    @Override
    public void registerCommand(IModule module, Object commandClass) {
        ModuloCore.getInstance().getCommandManager().registerCommand(commandClass);
        getCommands().computeIfAbsent(module, k -> new ObjectArrayList<>()).add(commandClass);
    }

    @Override
    public void registerAction(IModule module, Object actionClass) {
        ModuloCore.getInstance().getActionManager().registerAction(actionClass);
        getActions().computeIfAbsent(module, k -> new ObjectArrayList<>()).add(actionClass);
    }

    @Override
    public IModuleClassLoader getLoader(IModule module) {
        return this.loaders.get(module);
    }

    @Override
    public File getModuleDir() {
        return this.moduleDir;
    }

    @Override
    public Pattern getJarPattern() {
        return this.jarPattern;
    }

    @Override
    public Object2ObjectMap<IModule, IModuleClassLoader> getLoaders() {
        return this.loaders;
    }

    @Override
    public Object2ObjectMap<IModule, ObjectList<GloballyAttachableListener>> getListeners() {
        return this.listeners;
    }

    @Override
    public Object2ObjectMap<IModule, ObjectList<Object>> getCommands() {
        return this.commands;
    }

    @Override
    public Object2ObjectMap<String, Class<?>> getClasses() {
        return this.classes;
    }

    @Override
    public Object2ObjectMap<IModule, ObjectList<Object>> getActions() {
        return this.actions;
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
    public IModule loadModule(String module) {
        for (final File file : getModuleDir().listFiles()) {
            if (getJarPattern().matcher(file.getName()).matches()) {
                if (file.getName().toLowerCase().contains(module.toLowerCase())) {
                    try {
                        return loadModule(file);
                    } catch (InvalidModuleException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void reloadModule(IModule module) {
        if (!getModules().containsKey(module.getModuleInfo().getName()) || !module.isEnabled()) return;
        final var jarFile = module.getJarFile();
        disableModule(module);
        try {
            loadModule(jarFile);
        } catch (InvalidModuleException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disableModules() {
        if (!getEnabledModule().isEmpty()) {
            getEnabledModule().forEach(this::disableModule);
            ModuloCore.getInstance().getLogger().info("ModuloAPI - Modules: Successfully disabled.");
        }
        getCommands().clear();
        getActions().clear();
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
                ModuloCore.getInstance().getDiscordApi().removeListener(globallyAttachableListener);
            });
            getListeners().remove(module);
        }
        if (getCommands().containsKey(module)) {
            getCommands().get(module).forEach(command -> {
                ModuloCore.getInstance().getCommandManager().unregisterCommand(command);
            });
            getCommands().remove(module);
        }
        if (getActions().containsKey(module)) {
            getActions().get(module).forEach(action -> {
                ModuloCore.getInstance().getActionManager().unregisterAction(action);
            });
            getActions().remove(module);
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

    @Override
    public void downloadModuleFromAttachment(Attachment attachment) {
        if (!getJarPattern().matcher(attachment.getFileName()).matches()) return;

        for (final IModule value : getModules().values()) {
            if (value.getJarFile().getName().equalsIgnoreCase(attachment.getFileName())) {
                deleteModule(value);
            }
        }

        try (InputStream inputStream = attachment.asInputStream()) {
            final byte[] fileData = inputStream.readAllBytes();
            final FileOutputStream stream = new FileOutputStream(ModuloCore.getInstance().getModuleManager().getModuleDir() + "/" + attachment.getFileName());
            stream.write(fileData);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteModule(IModule module) {
        if (module == null) {
            return false;
        }
        if (module.isEnabled()) {
            disableModule(module);
        }
        if (module.getJarFile().delete()) {
            ModuloCore.getInstance().getLogger().info("ModuloAPI - Successfully deleted " + module.getModuleInfo().getName() + ".");
            return true;
        } else {
            ModuloCore.getInstance().getLogger().error("ModuloAPI - Error while deleting " + module.getModuleInfo().getName() + ".");
            return false;
        }
    }
}
