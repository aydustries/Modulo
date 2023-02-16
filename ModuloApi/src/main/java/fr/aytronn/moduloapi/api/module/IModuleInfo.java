package fr.aytronn.moduloapi.api.module;

/**
 * @author HookWoods
 */
public interface IModuleInfo {

    /**
     * Return the name of the module
     *
     * @return the name of the module
     */
    String getName();

    /**
     * Return the main class of the module
     *
     * @return the main class of the module
     */
    String getMain();

    /**
     * Return the description of the module
     *
     * @return the description of the module
     */
    String getDescription();

    /**
     * Return the version of the module
     *
     * @return the version of the module
     */
    String getVersion();

    /**
     * Return the author if alone
     *
     * @return the author if alone
     */
    String getAuthorsInLine();

    /**
     * Return the authors of the module
     *
     * @return the authors of the module
     */
    String[] getAuthors();
}
