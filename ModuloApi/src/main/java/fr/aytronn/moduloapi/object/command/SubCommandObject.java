package fr.aytronn.moduloapi.object.command;

import org.javacord.api.interaction.SlashCommandOptionType;

public class SubCommandObject {

    private final String subCommand;

    private String description;

    private String[] subCommandArgs;

    private SlashCommandOptionType[] subCommandArgsType;

    private boolean required = true;

    public SubCommandObject(String subCommand) {
        this.subCommand = subCommand;
    }

    public SubCommandObject(String command, String description) {
        this(command);
        this.description = description;
    }

    public SubCommandObject(String command, String description, String[] subCommandArgs, SlashCommandOptionType[] subCommandArgsType) {
        this(command);
        this.description = description;
        this.subCommandArgs = subCommandArgs;
        this.subCommandArgsType = subCommandArgsType;
    }

    public String getSubCommand() {
        return this.subCommand;
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

    public SlashCommandOptionType[] getSubCommandArgsType() {
        return this.subCommandArgsType;
    }

    public void setSubCommandArgsType(SlashCommandOptionType[] subCommandArgsType) {
        this.subCommandArgsType = subCommandArgsType;
    }

    public String[] getSubCommandArgs() {
        return this.subCommandArgs;
    }

    public void setSubCommandArgs(String[] subCommandArgs) {
        this.subCommandArgs = subCommandArgs;
    }
}
