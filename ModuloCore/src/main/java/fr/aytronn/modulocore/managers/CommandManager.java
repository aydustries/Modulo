package fr.aytronn.modulocore.managers;

import fr.aytronn.moduloapi.api.command.Command;
import fr.aytronn.moduloapi.api.command.CommandArgs;
import fr.aytronn.moduloapi.api.command.ICommandManager;
import fr.aytronn.moduloapi.object.command.SlashCommandObject;
import fr.aytronn.moduloapi.object.command.SubCommandGroupObject;
import fr.aytronn.moduloapi.object.command.SubCommandObject;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements ICommandManager {

    private final Map<String, SlashCommandObject> commands;

    private final Map<String, Map.Entry<Method, Object>> methods;

    public CommandManager() {
        this.commands = new HashMap<>();
        this.methods = new HashMap<>();
    }

    @Override
    public void registerCommand(Object classCommand) {
        for (final var m : classCommand.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) == null) continue;

            if (m.getParameterTypes().length > 1 || m.getParameterTypes()[0] != CommandArgs.class) {
                ModuloCore.getInstance().getLogger().warn("Unable to register command " + m.getName() + ". Unexpected method arguments");
                continue;
            }

            final Command command = m.getAnnotation(Command.class);
            if (command == null) continue;

            final String[] split = command.name().toLowerCase().split("\\.");

            if (split.length == 4 || split.length == 0) {
                ModuloCore.getInstance().getLogger().error("Command " + command.name() + " is not valid");
                continue;
            }

            SlashCommandObject slashCommandObject = getCommands().get(split[0]);

            if (slashCommandObject == null) {
                slashCommandObject = new SlashCommandObject(split[0]);
                getCommands().put(split[0], slashCommandObject);
            }

            switch (split.length) {
                case 1 -> slashCommandObject.setDescription(command.description());
                case 2 -> {
                    final SubCommandObject subCommandObject = new SubCommandObject(split[1], command.description(), command.subCommand(), command.subCommandType());
                    subCommandObject.setRequired(command.required());
                    slashCommandObject.addSubCommand(subCommandObject);
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

            final Map.Entry<Method, Object> entry = new AbstractMap.SimpleEntry<>(m, classCommand);
            getMethods().put(command.name(), entry);
        }
    }

    @Override
    public void loadCommands() {
        ModuloCore.getInstance().getLogger().info("Loading commands...");

        for (final SlashCommandObject slashCommandObject : getCommands().values()) {
            final List<SlashCommandOption> options = new ArrayList<>();

            for (final var subCommandGroup : slashCommandObject.getSubCommandGroups().values()) {
                final List<SlashCommandOption> subCommandOptions = new ArrayList<>();

                registerSubCommand(subCommandOptions, subCommandGroup.getSubCommands().values());

                options.add(SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND_GROUP, subCommandGroup.getSubCommandGroup(), subCommandGroup.getDescription(), subCommandOptions));
            }

            registerSubCommand(options, slashCommandObject.getSubCommand().values());

            ModuloCore.getInstance().getLogger().info("Load command: " + slashCommandObject.getCommand());
            SlashCommand.with(slashCommandObject.getCommand(), slashCommandObject.getDescription(), options)
                    .createForServer(ModuloCore.getInstance().getDiscordServer())
                    .join();
        }

        ModuloCore.getInstance().getLogger().info("Commands loaded");
    }

    private void registerSubCommand(List<SlashCommandOption> options, Collection<SubCommandObject> subCommandObjectList) {
        for (final var subCommand : subCommandObjectList) {
            if (subCommand.getSubCommandArgs().length != subCommand.getSubCommandArgsType().length) {
                ModuloCore.getInstance().getLogger().error(subCommand.getSubCommand() + " is not valid");
                continue;
            }
            final List<SlashCommandOption> subCommandArgsOptions = new ArrayList<>();
            for (int i = 0; i < subCommand.getSubCommandArgs().length; i++) {
                subCommandArgsOptions.add(SlashCommandOption.create(subCommand.getSubCommandArgsType()[i], subCommand.getSubCommandArgs()[i], subCommand.getDescription(), subCommand.isRequired()));
            }

            options.add(SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, subCommand.getSubCommand(), subCommand.getDescription(), subCommandArgsOptions));
        }
    }

    @Override
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

    @Override
    public void unregisterAllCommand() {
        ModuloCore.getInstance().getDiscordApi().getServerSlashCommands(ModuloCore.getInstance().getDiscordServer()).join().forEach(command -> command.delete().join());
    }

    @Override
    public Map<String, SlashCommandObject> getCommands() {
        return this.commands;
    }

    public Map<String, Map.Entry<Method, Object>> getMethods() {
        return this.methods;
    }
}
