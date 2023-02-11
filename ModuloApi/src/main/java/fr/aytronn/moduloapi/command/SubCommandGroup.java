package fr.aytronn.moduloapi.command;

import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.HashMap;
import java.util.Map;

public class SubCommandGroup {

    private final String subCommandGroup;

    private String description;

    private final Map<String, SubCommand> subCommands;

    private String[] subCommandArgs;

    private SlashCommandOptionType[] subCommandArgsType;

    public SubCommandGroup(String subCommandGroup) {
        this.subCommandGroup = subCommandGroup;
        this.subCommands = new HashMap<>();
    }

    public SubCommandGroup(String command, String description) {
        this(command);
        this.description = description;
    }

    public SubCommandGroup(String command, String description, String[] subCommandArgs, SlashCommandOptionType[] subCommandArgsType) {
        this(command);
        this.description = description;
        this.subCommandArgs = subCommandArgs;
        this.subCommandArgsType = subCommandArgsType;
    }

    public String getSubCommandGroup() {
        return this.subCommandGroup;
    }

    public Map<String, SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public void addSubCommand(SubCommand subCommand) {
        this.subCommands.put(subCommand.getSubCommand(), subCommand);
    }

    public String getDescription() {
        if (this.description == null) {
            return "Empty description";
        }
        return this.description;
    }

    public String[] getSubCommandArgs() {
        return this.subCommandArgs;
    }

    public SlashCommandOptionType[] getSubCommandArgsType() {
        return this.subCommandArgsType;
    }
}
