package fr.aytronn.moduloapi.command;

import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.HashMap;
import java.util.Map;

public class SubCommandGroupObject {

    private final String subCommandGroup;

    private String description;

    private final Map<String, SubCommandObject> subCommands;

    private String[] subCommandArgs;

    private SlashCommandOptionType[] subCommandArgsType;

    private boolean required = true;

    public SubCommandGroupObject(String subCommandGroup) {
        this.subCommandGroup = subCommandGroup;
        this.subCommands = new HashMap<>();
    }

    public SubCommandGroupObject(String command, String description) {
        this(command);
        this.description = description;
    }

    public SubCommandGroupObject(String command, String description, String[] subCommandArgs, SlashCommandOptionType[] subCommandArgsType) {
        this(command);
        this.description = description;
        this.subCommandArgs = subCommandArgs;
        this.subCommandArgsType = subCommandArgsType;
    }

    public String getSubCommandGroup() {
        return this.subCommandGroup;
    }

    public Map<String, SubCommandObject> getSubCommands() {
        return this.subCommands;
    }

    public void addSubCommand(SubCommandObject subCommandObject) {
        this.subCommands.put(subCommandObject.getSubCommand(), subCommandObject);
    }

    public String getDescription() {
        if (this.description == null) {
            return "Empty description";
        }
        return this.description;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String[] getSubCommandArgs() {
        return this.subCommandArgs;
    }

    public SlashCommandOptionType[] getSubCommandArgsType() {
        return this.subCommandArgsType;
    }
}
