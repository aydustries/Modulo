package fr.aytronn.moduloapi.object.command;

import org.javacord.api.interaction.SlashCommand;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandObject {
    private final String command;
    private String description;
    private final Map<String, SubCommandGroupObject> subCommandGroups;
    private final Map<String, SubCommandObject> subCommand;

    private SlashCommand slashCommand;

    public SlashCommandObject(String command) {
        this.command = command;
        this.subCommandGroups = new HashMap<>();
        this.subCommand = new HashMap<>();
    }

    public SlashCommandObject(String command, String description) {
        this(command);
        this.description = description;
    }

    public String getCommand() {
        return this.command;
    }

    public Map<String, SubCommandGroupObject> getSubCommandGroups() {
        return this.subCommandGroups;
    }

    public void addSubCommandGroup(SubCommandGroupObject subCommandGroupObject) {
        this.subCommandGroups.put(subCommandGroupObject.getSubCommandGroup(), subCommandGroupObject);
    }

    public void addSubCommand(SubCommandObject subCommandObject) {
        this.subCommand.put(subCommandObject.getSubCommand(), subCommandObject);
    }

    public Map<String, SubCommandObject> getSubCommand() {
        return this.subCommand;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        if (this.description == null) {
            return "Empty description";
        }
        return this.description;
    }

    public void setSlashCommand(SlashCommand slashCommand) {
        this.slashCommand = slashCommand;
    }

    public SlashCommand getSlashCommand() {
        return this.slashCommand;
    }
}
