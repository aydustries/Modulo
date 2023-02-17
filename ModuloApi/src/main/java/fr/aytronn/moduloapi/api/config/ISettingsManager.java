package fr.aytronn.moduloapi.api.config;

public interface ISettingsManager {

    /**
     * Allow to save the settings
     */
    void saveSettings();

    /**
     * Allow to get the settings
     *
     * @return the settings
     */
    Settings getSettings();
}
