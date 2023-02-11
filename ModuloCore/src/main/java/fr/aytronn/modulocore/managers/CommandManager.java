package fr.aytronn.modulocore.managers;

import fr.aytronn.modulocore.ModuloCore;
import fr.aytronn.moduloapi.command.CommandArgs;
import fr.aytronn.moduloapi.command.SlashCommand;
import fr.aytronn.moduloapi.command.SubCommand;
import fr.aytronn.moduloapi.command.SubCommandGroup;
import fr.aytronn.moduloapi.command.Command;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    Map<String, SlashCommand> commands;

    Map<String, Map.Entry<Method, Object>> methods;

    public CommandManager() {
        this.commands = new HashMap<>();
        this.methods = new HashMap<>();
    }

    public void registerCommand(Object classCommand) {
        for (final var m : classCommand.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) == null) continue;

            if (m.getParameterTypes().length > 1 || m.getParameterTypes()[0] != CommandArgs.class) {
                ModuloCore.getInstance().getLogger().warn("Unable to register command " + m.getName() + ". Unexpected method arguments");
                continue;
            }

            final var command = m.getAnnotation(Command.class);
            if (command == null) continue;

            final String[] split = command.name().split("\\.");

            if (split.length == 4 || split.length == 0) {
                ModuloCore.getInstance().getLogger().error("Command " + command.name() + " is not valid");
                continue;
            }

            SlashCommand slashCommand = getCommands().get(split[0]);

            if (slashCommand == null) {
                slashCommand = new SlashCommand(split[0]);

                switch (split.length) {
                    case 1 -> slashCommand.setDescription(command.description());
                    case 2 -> slashCommand.addSubCommandGroup(new SubCommandGroup(split[1], command.description(), command.subCommand(), command.subCommandType()));
                    case 3 -> {
                        final SubCommandGroup subCommandGroup = new SubCommandGroup(split[1], command.description());
                        subCommandGroup.addSubCommand(new SubCommand(split[2], command.description(), command.subCommand(), command.subCommandType()));
                        slashCommand.addSubCommandGroup(subCommandGroup);
                    }
                }
                getCommands().put(split[0], slashCommand);
            } else {
                switch (split.length) {
                    case 2 -> {
                        slashCommand.addSubCommandGroup(new SubCommandGroup(split[1], command.description(), command.subCommand(), command.subCommandType()));
                    }
                    case 3 -> {
                        SubCommandGroup subCommandGroup = slashCommand.getSubCommandGroups().get(split[1]);

                        if (subCommandGroup == null) {
                            subCommandGroup = new SubCommandGroup(split[1], command.description());
                        }

                        subCommandGroup.addSubCommand(new SubCommand(split[2], command.description(), command.subCommand(), command.subCommandType()));
                        slashCommand.addSubCommandGroup(subCommandGroup);
                    }
                }
            }
            final Map.Entry<Method, Object> entry = new AbstractMap.SimpleEntry<>(m, classCommand);
            getMethods().put(command.name(), entry);
        }
    }

    public void loadCommands() {
        ModuloCore.getInstance().getLogger().info("Loading commands...");

        for (final var entry : getCommands().entrySet()) {

            final SlashCommand slashCommand = entry.getValue();

            final List<SlashCommandOption> options = new ArrayList<>();

            for (final var subCommandGroup : slashCommand.getSubCommandGroups().values()) {

                final List<SlashCommandOption> subCommandOptions = new ArrayList<>();

                for (final var subCommand : subCommandGroup.getSubCommands().values()) {
                    if (subCommand.getSubCommandArgs().length != subCommand.getSubCommandArgsType().length) {
                        ModuloCore.getInstance().getLogger().error("SubCommand " + slashCommand.getCommand() + " " + subCommandGroup.getSubCommandGroup() + " " + subCommand.getSubCommand() + " is not valid");
                        continue;
                    }

                    final List<SlashCommandOption> subCommandArgsOptions = new ArrayList<>();
                    for (int i = 0; i < subCommand.getSubCommandArgs().length; i++) {
                        subCommandArgsOptions.add(
                                SlashCommandOption.create(
                                        subCommand.getSubCommandArgsType()[i],
                                        subCommand.getDescription(),
                                        subCommand.getSubCommandArgs()[i],
                                        true
                                )
                        );
                    }

                    subCommandOptions.add(
                            SlashCommandOption.createWithOptions(
                                    SlashCommandOptionType.SUB_COMMAND,
                                    subCommand.getSubCommand(),
                                    subCommand.getDescription(),
                                    subCommandArgsOptions
                            )
                    );
                }

                if (subCommandOptions.isEmpty()) {
                    if (subCommandGroup.getSubCommandArgs().length != subCommandGroup.getSubCommandArgsType().length) {
                        ModuloCore.getInstance().getLogger().error("SubCommand " + slashCommand.getCommand() + " " + subCommandGroup.getSubCommandGroup() + " is not valid");
                        continue;
                    }

                    final List<SlashCommandOption> subCommandArgsOptions = new ArrayList<>();
                    for (int i = 0; i < subCommandGroup.getSubCommandArgs().length; i++) {
                        subCommandArgsOptions.add(
                                SlashCommandOption.create(
                                        subCommandGroup.getSubCommandArgsType()[i],
                                        subCommandGroup.getSubCommandArgs()[i],
                                        subCommandGroup.getDescription(),
                                        true
                                )
                        );
                    }
                    options.add(
                            SlashCommandOption.createWithOptions(
                                    SlashCommandOptionType.SUB_COMMAND,
                                    subCommandGroup.getSubCommandGroup(),
                                    subCommandGroup.getDescription(),
                                    subCommandArgsOptions
                            )
                    );
                } else {
                    options.add(
                            SlashCommandOption.createWithOptions(
                                    SlashCommandOptionType.SUB_COMMAND_GROUP,
                                    subCommandGroup.getSubCommandGroup(),
                                    subCommandGroup.getDescription(),
                                    subCommandOptions
                            )
                    );
                }
            }

            ModuloCore.getInstance().getLogger().info("Load command: " + slashCommand.getCommand());
            org.javacord.api.interaction.SlashCommand.with(slashCommand.getCommand(), slashCommand.getDescription(), options)
                    .createGlobal(ModuloCore.getInstance().getDiscordApi())
                    .join();
        }

        ModuloCore.getInstance().getLogger().info("Commands loaded");
    }

    public Map<String, SlashCommand> getCommands() {
        return this.commands;
    }

    public Map<String, Map.Entry<Method, Object>> getMethods() {
        return this.methods;
    }
}
