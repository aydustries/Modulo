package fr.aytronn.modulocore.commands;

import fr.aytronn.moduloapi.command.Command;
import fr.aytronn.moduloapi.command.CommandArgs;
import fr.aytronn.moduloapi.modules.IModule;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoreCommand {

    @Command(name = "modulo.stop", description = "Stop the bot", subCommandType = {SlashCommandOptionType.LONG}, subCommand = {"time"}, required = false)
    public void stopCommand(CommandArgs args) {
        if (args.getCommandInteraction().getArguments().size() > 0) {
            final long time = args.getCommandInteraction().getArguments().get(0).getLongValue().orElse(0L);
            if (time > 0) {
                args.reply("ModuloCore is stopping in " + time + "s...");

                new Thread(() -> {
                    try {
                        Thread.sleep(time * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ModuloCore.getInstance().stop();
                }).start();
                return;
            }
        }

        args.reply("ModuloCore is stopping...");
        ModuloCore.getInstance().stop();
    }

    @Command(name = "modulo.module.list", description = "Get the list of module", subCommandType = {SlashCommandOptionType.STRING}, subCommand = {"filter"}, required = false)
    public void moduleListCommand(CommandArgs args) {
        String filter = "";
        if (args.getCommandInteraction().getArguments().size() > 0) {
            filter = args.getCommandInteraction().getArguments().get(0).getStringValue().orElse("");
        }

        if (args.getChannel().isEmpty()) {
            args.reply("You must execute this command in a channel.");
            return;
        }
        final AtomicBoolean moduleFound = new AtomicBoolean(false);
        final String finalFilter = filter;
        ModuloCore.getInstance().getModuleManager().getModules().forEach((name, module) -> {
            if (!module.getModuleInfo().getName().toLowerCase().contains(finalFilter.toLowerCase())) {
                return;
            }
            moduleFound.set(true);
            final EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(module.getModuleInfo().getName())
                    .setDescription(module.getModuleInfo().getDescription())
                    .setAuthor(module.getModuleInfo().getAuthorsInLine())
                    .addField("State", module.getState().name())
                    .setColor(Color.BLUE);

            new MessageBuilder()
                    .setEmbed(embed)
                    .addComponents(ActionRow.of(
                            Button.success("start@" + module.getModuleInfo().getName(), "Start"),
                            Button.secondary("reload@" + module.getModuleInfo().getName(), "Reload"),
                            Button.danger("stop@" + module.getModuleInfo().getName(), "Stop"),
                            Button.danger("delete@" + module.getModuleInfo().getName(), "Delete")
                    )).send(args.getChannel().get());
        });

        if (!moduleFound.get()) {
            args.reply("No module found with the filter: " + filter);
        } else {
            args.reply("All modules found with the filter: " + filter);
        }
    }

    @Command(name = "modulo.module.disable", description = "Disable module", subCommandType = {SlashCommandOptionType.STRING}, subCommand = {"module"}, required = true)
    public void disableModule(CommandArgs args) {
        if (!(args.getCommandInteraction().getArguments().size() > 0)) {
            args.reply("Invalid parameter.");
            return;
        }
        final String moduleName = args.getCommandInteraction().getArguments().get(0).getStringValue().orElse("");

        final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);

        if (module == null) {
            args.reply("Module " + moduleName + " not found.");
            return;
        }

        ModuloCore.getInstance().getModuleManager().disableModule(module);

        args.reply("Module " + moduleName + " disabled.");
    }

    @Command(name = "modulo.module.disables", description = "Disable all module")
    public void disableAllModule(CommandArgs args) {
        ModuloCore.getInstance().getModuleManager().disableModules();
        args.reply("All modules disabled.");
    }

    @Command(name = "modulo.module.enable", description = "Enable module", subCommandType = {SlashCommandOptionType.STRING}, subCommand = {"module"}, required = true)
    public void enableModule(CommandArgs args) {
        if (!(args.getCommandInteraction().getArguments().size() > 0)) {
            args.reply("Invalid parameter.");
            return;
        }
        final String moduleName = args.getCommandInteraction().getArguments().get(0).getStringValue().orElse("");

        final boolean b = ModuloCore.getInstance().getModuleManager().loadModule(moduleName);

        if (b) {
            args.reply("Module " + moduleName + " disabled.");
        } else {
            args.reply("Module " + moduleName + " not found.");
        }
    }

    @Command(name = "modulo.module.enables", description = "Enable all module")
    public void enableAllModule(CommandArgs args) {
        ModuloCore.getInstance().getModuleManager().loadModules();
        args.reply("All modules enabled.");
    }

    @Command(name = "modulo.module.reload", description = "Reload module", subCommandType = {SlashCommandOptionType.STRING}, subCommand = {"module"})
    public void reloadModule(CommandArgs args) {
        final IModule module = getiModule(args);
        if (module == null) return;

        ModuloCore.getInstance().getModuleManager().reloadModule(module);

        args.reply("Module " + module.getModuleInfo().getName() + " reloaded.");
    }

    @Command(name = "modulo.module.reloads", description = "Reload all module")
    public void reloadAllModule(CommandArgs args) {
        ModuloCore.getInstance().getModuleManager().reloadModules();
        args.reply("All modules reloaded.");
    }

    @Nullable
    private static IModule getiModule(CommandArgs args) {
        if (!(args.getCommandInteraction().getArguments().size() > 0)) {
            args.reply("Invalid parameter.");
            return null;
        }
        final String moduleName = args.getCommandInteraction().getArguments().get(0).getStringValue().orElse("");

        final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);

        if (module == null) {
            args.reply("Module " + moduleName + " not found.");
            return null;
        }
        return module;
    }

    @Command(name = "modulo.module.add", description = "Add module", subCommandType = {SlashCommandOptionType.ATTACHMENT}, subCommand = {"module"})
    public void addModule(CommandArgs args) {
        if (!(args.getCommandInteraction().getArguments().size() > 0)) {
            args.reply("Invalid parameter.");
            return;
        }

        args.getCommandInteraction().getArguments().get(0).getAttachmentValue().ifPresent(messageAttachment -> {
            if (!ModuloCore.getInstance().getModuleManager().getJarPattern().matcher(messageAttachment.getFileName()).matches()) {
                args.reply("Invalid jar file.");
                return;
            }

            ModuloCore.getInstance().getModuleManager().downloadModuleFromAttachment(messageAttachment);

            final boolean b = ModuloCore.getInstance().getModuleManager().loadModule(messageAttachment.getFileName().replace(".jar", ""));
            if (!b) {
                args.reply("Module " + messageAttachment.getFileName() + " not found.");
                return;
            }

            args.reply("Loaded module " + messageAttachment.getFileName() + ".");
        });
    }

    @Command(name = "modulo.module.remove", description = "Remove module", subCommandType = {SlashCommandOptionType.STRING}, subCommand = {"module"})
    public void removeModule(CommandArgs args) {
        if (!(args.getCommandInteraction().getArguments().size() > 0)) {
            args.reply("Invalid parameter.");
            return;
        }

        final String moduleName = args.getCommandInteraction().getArguments().get(0).getStringValue().orElse("");

        final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);

        if (module == null) {
            args.reply("Module " + moduleName + " not found.");
            return;
        }

        final boolean b = ModuloCore.getInstance().getModuleManager().deleteModule(module);

        if (b) {
            args.reply("Module " + moduleName + " removed.");
        } else {
            args.reply("Module " + moduleName + " not found.");
        }
    }
}
