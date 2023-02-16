package fr.aytronn.moduloapi.api.module;

import fr.aytronn.moduloapi.exceptions.module.InvalidDescriptionException;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.io.File;
import java.io.IOException;

/**
 * @author HookWoods
 */
public interface IModuleClassLoader {

    /**
     * Useful to get the module info class
     *
     * @param file the file of the module to get
     *
     * @return the module info class of the module
     *
     * @throws InvalidDescriptionException if module doesn't contains or corrupted modules.yml
     */
    IModuleInfo getDescription(File file) throws InvalidDescriptionException, InvalidDescriptionException;

    /**
     * Useful to check if a module contains a class
     *
     * @return the java classes of the module
     */
    ObjectSet<String> getClasses();

    /**
     * Useful to get the module
     *
     * @return the module class
     */
    IModule getModule();

    /**
     * Useful to save data or libs to a module folder
     * Like bukkit system
     *
     * @return the data folder of the module
     */
    File getDataFolder();

    /**
     * Useful cause bukkit class loader
     * totally broke module class loader
     *
     * @return the java class loader
     */
    ClassLoader getClassLoader();

    /**
     * Useful to get or load a Class of a module
     *
     * @param name of the class
     * @param checkGlobal check in the module or jvm cache
     *
     * @return the class if find
     */
    Class<?> findClass(String name, boolean checkGlobal);

    /**
     * Useful to close the ClassLoader
     * and avoid memory leak
     *
     * @throws IOException if the module is corrupted
     */
    void close() throws IOException;

}
