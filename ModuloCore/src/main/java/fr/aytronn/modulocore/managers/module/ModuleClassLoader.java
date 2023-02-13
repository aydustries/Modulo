package fr.aytronn.modulocore.managers.module;

import fr.aytronn.moduloapi.modules.IModule;
import fr.aytronn.moduloapi.modules.IModuleClassLoader;
import fr.aytronn.moduloapi.modules.IModuleInfo;
import fr.aytronn.moduloapi.modules.IModuleManager;
import fr.aytronn.moduloapi.modules.exception.InvalidDescriptionException;
import fr.aytronn.moduloapi.modules.exception.InvalidModuleException;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

/**
 * @author HookWoods
 */
public class ModuleClassLoader extends URLClassLoader implements IModuleClassLoader {

    private final File dataFolder;
    private final IModule module;
    private final Object2ObjectMap<String, Class<?>> classes;
    private final IModuleManager moduleManager;

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ModuleClassLoader(ClassLoader parent, File file, File dataFolder, IModuleManager moduleManager) throws InvalidModuleException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.dataFolder = dataFolder;
        this.classes = new Object2ObjectOpenHashMap<>();
        this.moduleManager = moduleManager;

        try {
            final IModuleInfo info = getDescription(file);
            final Class<?> moduleClass = Class.forName(info.getMain(), true, this);
            final Class<? extends IModule> mainClass = moduleClass.asSubclass(IModule.class);
            this.module = mainClass.getDeclaredConstructor().newInstance();
            this.module.setModuleInfo(info);
            this.module.setJar(file);
        } catch (Exception throwable) {
            throwable.printStackTrace();
            throw new InvalidModuleException("Can't find any main class");
        }

        initialize();
    }

    @Override
    public ModuleInfo getDescription(File file) throws InvalidDescriptionException {
        try (final JarFile jar = new JarFile(file)) {
            final var entry = jar.getJarEntry("module.yml");
            if (entry == null)
                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain module.yml"));

            try (final var stream = jar.getInputStream(entry)) {
                return new ModuleInfo(stream);
            }
        } catch (Exception ex) {
            throw new InvalidDescriptionException(ex);
        }
    }

    private void initialize() {
        if (getModule().isEnabled()) {
            throw new IllegalArgumentException("Module already initialized!");
        } else {
            try {
                getModule().init(this, getDataFolder());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) {
        return findClass(name, true);
    }

    @Override
    public Class<?> findClass(String name, boolean checkGlobal) {
        Class<?> result = this.classes.get(name);
        if (result == null) {
            if (checkGlobal) {
                result = this.moduleManager.getClassByName(name);
            }

            if (result == null) {
                try {
                    result = super.findClass(name);
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // Do nothing.
                }
                if (result != null) {
                    this.moduleManager.setClass(name, result);

                }
            }
            this.classes.put(name, result);
        }
        return result;
    }

    @Override
    public ObjectSet<String> getClasses() {
        return this.classes.keySet();
    }

    @Override
    public IModule getModule() {
        return this.module;
    }

    @Override
    public File getDataFolder() {
        return this.dataFolder;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this;
    }
}

