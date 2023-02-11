package fr.aytronn.moduloapi.api;

public interface Settings {

    /**
     * Useful to convert from the config
     * to the database config credentials
     *
     * @return the factory object of the credentials
     */
    public Object toFactory();

}
