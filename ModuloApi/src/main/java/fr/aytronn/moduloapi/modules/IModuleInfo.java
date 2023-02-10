package fr.aytronn.moduloapi.modules;

public interface IModuleInfo {

    /**
     * Return the name of the module
     *
     * @return the name of the module
     */
    public String getName();

    /**
     * Return the main class of the module
     *
     * @return the main class of the module
     */
    public String getMain();

    /**
     * Return the description of the module
     *
     * @return the description of the module
     */
    public String getDescription();

    /**
     * Return the version of the module
     *
     * @return the version of the module
     */
    public String getVersion();

    /**
     * Return the author if alone
     *
     * @return the author if alone
     */
    public String getAuthorsInLine();

    /**
     * Return the authors of the module
     *
     * @return the authors of the module
     */
    public String[] getAuthors();
}
