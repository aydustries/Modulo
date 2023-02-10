package fr.aytronn.moduloapi.command;

import org.javacord.api.interaction.SlashCommandOptionType;

public class SubCommand {

    private final String subCommand;

    private String description;

    private String[] subCommandArgs;

    private SlashCommandOptionType[] subCommandArgsType;

    public SubCommand(String subCommand) {
        this.subCommand = subCommand;
    }

    public SubCommand(String command, String description) {
        this(command);
        this.description = description;
    }

    public SubCommand(String command, String description, String[] subCommandArgs, SlashCommandOptionType[] subCommandArgsType) {
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
