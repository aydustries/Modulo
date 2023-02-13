package fr.aytronn.moduloapi.command;

import java.util.Map;

public interface ICommandManager {

    /**
     * Useful to register a command
     *
     * @param classCommand the class of the command
     */
    void registerCommand(Object classCommand);

    /**
     * Useful to load all commands
     */
    void loadCommands();

    /**
     * Useful to unregister a command
     *
     * @param object the object of the command
     */
    void unregisterCommand(Object object);

    /**
     * Useful to unregister all commands
     */
    void unregisterAllCommand();

    /**
     * Useful to get the commands
     *
     * @return the commands
     */
    Map<String, SlashCommandObject> getCommands();
}
