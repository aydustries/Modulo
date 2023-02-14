package fr.aytronn.moduloapi.api;

/**
 * @author HookWoods
 */
public interface Settings {

    /**
     * Useful to convert from the config
     * to the database config credentials
     *
     * @return the factory object of the credentials
     */
    Object toFactory();

}
