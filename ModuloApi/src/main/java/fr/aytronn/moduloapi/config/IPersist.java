package fr.aytronn.moduloapi.config;

import java.io.File;
import java.lang.reflect.Type;

/**
 * @author HookWoods
 */
public interface IPersist {

    /**
     * This function is used to get a
     * name from it's class
     *
     * @param clazz of the type
     *
     * @return the name
     */
    public String getName(Class<?> clazz);

    /**
     * This function is used to get a
     * name from it's object class
     *
     * @param object of the type
     *
     * @return the name
     */
    public default String getName(Object object) {
        return getName(object.getClass());
    }

    /**
     * This function is used to get a
     * name from it's type class
     *
     * @param type of the type
     *
     * @return the name
     */
    public default String getName(Type type) {
        return getName(type.getClass());
    }

    /**
     * This function is used to get a
     * file from it's name
     *
     * @param name of the file
     *
     * @return the file
     */
    public File getFile(String name);

    /**
     * This function is used to get a
     * file from it's class name
     *
     * @param objectClass of the file
     *
     * @return the file
     */
    public default File getFile(Class<?> objectClass) {
        return getFile(getName(objectClass));
    }

    /**
     * This function is used to get a
     * file from it's object name
     *
     * @param object of the file
     *
     * @return the file
     */
    public default File getFile(Object object) {
        return getFile(getName(object));
    }

    /**
     * This function is used to get a
     * file from it's type class name
     *
     * @param type of the file
     *
     * @return the file
     */
    public default File getFile(Type type) {
        return getFile(getName(type));
    }

    /**
     * This function is used to save the instance of a class
     * to the data folder folder
     *
     * @param instance of the class to save
     *
     * @param file file name to save
     */
    public void save(Object instance, File file);

    /**
     * This function is used to save the instance of a class
     * to the data folder folder with its name in lowercase
     *
     * @param instance of the class to save
     */
    public default void save(Object instance) {
        save(instance, getFile(instance));
    }

    /**
     * ZAKARY PRIVATE
     *
     * @param tClass instance to load
     * @param <T> Class to load
     *
     * @return the loaded class
     */
    public default <T> T basicLoad(Class<T> tClass) {
        return load(tClass, getFile(tClass));
    }

    /**
     * This function is used to load a class
     * from server config dir
     *
     * @param tClass instance to load
     * @param file name to load
     * @param <T> Class to load
     *
     * @return the loaded class
     */
    public <T> T load(Class<T> tClass, File file);

    public String load(File file);

    /**
     * This function is used to load a class
     * from server config dir
     *
     * @param tClass instance to load
     * @param content name to load
     * @param <T> Class to load
     *
     * @return the loaded class
     */
    public <T> T load(Class<T> tClass, String content);

    /**
     * This function is used to load a class
     * from server config dir
     *
     * @param tClass instance to load
     * @param <T> Class to load
     *
     * @return the loaded class
     */
    public default <T> T load(Class<T> tClass) {
        return load(tClass, getFile(tClass));
    }
}
