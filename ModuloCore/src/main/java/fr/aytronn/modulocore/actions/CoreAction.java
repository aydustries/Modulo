package fr.aytronn.modulocore.actions;

import fr.aytronn.moduloapi.api.action.Action;
import fr.aytronn.moduloapi.api.action.ActionArgs;
import fr.aytronn.moduloapi.api.module.IModule;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.interaction.SelectMenuInteraction;

public class CoreAction {

    @Action(customId = "start")
    public void startModuleAction(ActionArgs args) {
        if (args.getArgs().size() < 1) {
            args.reply("An exception occurred while executing this command");
            throw new IllegalArgumentException("Missing argument: " + this.getClass().getName() + ".startModuloAction()");
        }
        final String moduleName = args.getArgs().get(0);
        final IModule module = ModuloCore.getInstance().getModuleManager().loadModule(moduleName);
        if (module == null) {
            args.reply("Module " + moduleName + " not found!");
            return;
        }
        args.getEvent().getMessageComponentInteraction().getMessage().createUpdater().setEmbed(module.getEmbed()).applyChanges();
        args.reply("Module " + moduleName + " loaded!");
    }

    @Action(customId = "stop")
    public void stopModuleAction(ActionArgs args) {
        if (args.getArgs().size() < 1) {
            args.reply("An exception occurred while executing this command");
            throw new IllegalArgumentException("Missing argument: " + this.getClass().getName() + ".stopModuleAction()");
        }
        final String moduleName = args.getArgs().get(0);
        final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);
        if (module == null) {
            args.reply("Module " + moduleName + " not found!");
            return;
        }

        ModuloCore.getInstance().getModuleManager().disableModule(module);
        args.getEvent().getMessageComponentInteraction().getMessage().createUpdater().setEmbed(module.getEmbed()).applyChanges();
        args.reply("Module " + moduleName + " disabled!");
    }

    @Action(customId = "reload")
    public void reloadModuleAction(ActionArgs args) {
        if (args.getArgs().size() < 1) {
            args.reply("An exception occurred while executing this command");
            throw new IllegalArgumentException("Missing argument: " + this.getClass().getName() + ".reloadModuleAction()");
        }
        final String moduleName = args.getArgs().get(0);
        final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);
        if (module == null) {
            args.reply("Module " + moduleName + " not found!");
            return;
        }

        ModuloCore.getInstance().getModuleManager().reloadModule(module);
        args.getEvent().getMessageComponentInteraction().getMessage().createUpdater().setEmbed(module.getEmbed()).applyChanges();
        args.reply("Module " + moduleName + " reloaded!");
    }

    @Action(customId = "delete")
    public void deleteModuleAction(ActionArgs args) {
        if (args.getArgs().size() < 1) {
            args.reply("An exception occurred while executing this command");
            throw new IllegalArgumentException("Missing argument: " + this.getClass().getName() + ".deleteModuleAction()");
        }
        final String moduleName = args.getArgs().get(0);
        final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);
        if (module == null) {
            args.reply("Module " + moduleName + " not found!");
            return;
        }

        ModuloCore.getInstance().getModuleManager().deleteModule(module);
        args.getEvent().getMessageComponentInteraction().getMessage().delete();
        args.reply("Module " + moduleName + " deleted!");
    }

    @Action(customId = "selectChannelUpdater")
    public void settingsChannelUpdaterAction(ActionArgs args) {
        if (args.getEvent().getMessageComponentInteraction().asSelectMenuInteraction().isEmpty()) return;
        final SelectMenuInteraction selectMenuInteraction = args.getEvent().getMessageComponentInteraction().asSelectMenuInteraction().get();

        if (selectMenuInteraction.getSelectedChannels().size() != 1) {
            args.reply("Missing argument your channel choice.");
            return;
        }

        args.getEvent().getMessageComponentInteraction().getMessage().delete();

        final ServerChannel serverChannel = selectMenuInteraction.getSelectedChannels().get(0);

        ModuloCore.getInstance().getSettingsManager().getSettings().setChannelUpdater(serverChannel.getId());
        ModuloCore.getInstance().getSettingsManager().saveSettings();
        args.reply("Channel updater set to " + serverChannel.getName() + "!");
    }
}
