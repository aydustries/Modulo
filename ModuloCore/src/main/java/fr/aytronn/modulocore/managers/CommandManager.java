package fr.aytronn.modulocore.managers;

import fr.aytronn.modulocore.ModuloCore;
import fr.aytronn.moduloapi.command.CommandArgs;
import fr.aytronn.moduloapi.command.SlashCommandObject;
import fr.aytronn.moduloapi.command.SubCommandObject;
import fr.aytronn.moduloapi.command.SubCommandGroupObject;
import fr.aytronn.moduloapi.command.Command;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    Map<String, SlashCommandObject> commands;

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

            SlashCommandObject slashCommandObject = getCommands().get(split[0]);

            if (slashCommandObject == null) {
                slashCommandObject = new SlashCommandObject(split[0]);

                switch (split.length) {
                    case 1 -> slashCommandObject.setDescription(command.description());
                    case 2 -> {
                        final SubCommandGroupObject subCommandGroupObject = new SubCommandGroupObject(split[1], command.description(), command.subCommand(), command.subCommandType());
                        subCommandGroupObject.setRequired(command.required());
                        slashCommandObject.addSubCommandGroup(subCommandGroupObject);
                    }
                    case 3 -> {
                        final SubCommandGroupObject subCommandGroupObject = new SubCommandGroupObject(split[1], command.description());
                        final SubCommandObject subCommandObject = new SubCommandObject(split[2], command.description(), command.subCommand(), command.subCommandType());
                        subCommandObject.setRequired(command.required());
                        subCommandGroupObject.addSubCommand(subCommandObject);
                        slashCommandObject.addSubCommandGroup(subCommandGroupObject);
                    }
                }
                getCommands().put(split[0], slashCommandObject);
            } else {
                switch (split.length) {
                    case 2 -> {
                        final SubCommandGroupObject subCommandGroupObject = new SubCommandGroupObject(split[1], command.description(), command.subCommand(), command.subCommandType());
                        subCommandGroupObject.setRequired(command.required());
                        slashCommandObject.addSubCommandGroup(subCommandGroupObject);
                    }
                    case 3 -> {
                        SubCommandGroupObject subCommandGroupObject = slashCommandObject.getSubCommandGroups().get(split[1]);

                        if (subCommandGroupObject == null) {
                            subCommandGroupObject = new SubCommandGroupObject(split[1], command.description());
                        }

                        final SubCommandObject subCommandObject = new SubCommandObject(split[2], command.description(), command.subCommand(), command.subCommandType());
                        subCommandObject.setRequired(command.required());

                        subCommandGroupObject.addSubCommand(subCommandObject);
                        slashCommandObject.addSubCommandGroup(subCommandGroupObject);
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

            final SlashCommandObject slashCommandObject = entry.getValue();

            final List<SlashCommandOption> options = new ArrayList<>();

            for (final var subCommandGroup : slashCommandObject.getSubCommandGroups().values()) {

                final List<SlashCommandOption> subCommandOptions = new ArrayList<>();

                for (final var subCommand : subCommandGroup.getSubCommands().values()) {
                    if (subCommand.getSubCommandArgs().length != subCommand.getSubCommandArgsType().length) {
                        ModuloCore.getInstance().getLogger().error("SubCommand " + slashCommandObject.getCommand() + " " + subCommandGroup.getSubCommandGroup() + " " + subCommand.getSubCommand() + " is not valid");
                        continue;
                    }

                    final List<SlashCommandOption> subCommandArgsOptions = new ArrayList<>();
                    for (int i = 0; i < subCommand.getSubCommandArgs().length; i++) {
                        subCommandArgsOptions.add(
                                SlashCommandOption.create(
                                        subCommand.getSubCommandArgsType()[i],
                                        subCommand.getDescription(),
                                        subCommand.getSubCommandArgs()[i],
                                        subCommand.isRequired()
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
                        ModuloCore.getInstance().getLogger().error("SubCommand " + slashCommandObject.getCommand() + " " + subCommandGroup.getSubCommandGroup() + " is not valid");
                        continue;
                    }

                    final List<SlashCommandOption> subCommandArgsOptions = new ArrayList<>();
                    for (int i = 0; i < subCommandGroup.getSubCommandArgs().length; i++) {
                        subCommandArgsOptions.add(
                                SlashCommandOption.create(
                                        subCommandGroup.getSubCommandArgsType()[i],
                                        subCommandGroup.getSubCommandArgs()[i],
                                        subCommandGroup.getDescription(),
                                        subCommandGroup.isRequired()
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

            ModuloCore.getInstance().getLogger().info("Load command: " + slashCommandObject.getCommand());
            SlashCommand.with(slashCommandObject.getCommand(), slashCommandObject.getDescription(), options)
                    .createForServer(ModuloCore.getInstance().getDiscordServer())
                    .join();
        }

        ModuloCore.getInstance().getLogger().info("Commands loaded");
    }

    public void unregisterCommand(Object object) {
        String name = null;

        for (final var entry : getMethods().entrySet()) {
            if (entry.getValue().getValue().equals(object)) {
                name = entry.getKey();
                break;
            }
        }

        if (name == null) {
            return;
        }

        final String finalName = name;
        ModuloCore.getInstance().getDiscordApi().getServerApplicationCommands(ModuloCore.getInstance().getDiscordServer()).join().forEach(command -> {
            if (command.getName().equals(finalName)) {
                command.delete().join();
                getMethods().remove(finalName);
            }
        });
    }

    public void unregisterAllCommand() {
        ModuloCore.getInstance().getDiscordApi().getServerSlashCommands(ModuloCore.getInstance().getDiscordServer()).join().forEach(command -> command.delete().join());
    }

    public Map<String, SlashCommandObject> getCommands() {
        return this.commands;
    }

    public Map<String, Map.Entry<Method, Object>> getMethods() {
        return this.methods;
    }
}
