package fr.aytronn.modulocore.listeners;

import fr.aytronn.moduloapi.modules.IModule;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

public class InteractionListener implements MessageComponentCreateListener {

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        if (event.getInteraction().getUser().isBot()) return;
        if (event.getInteraction().getChannel().isEmpty()) return;

        final String[] split = event.getMessageComponentInteraction().getCustomId().split("@");

        if (split.length == 2) {
            final String customId = split[0];
            final String moduleName = split[1];

            final IModule module = ModuloCore.getInstance().getModuleManager().getModule(moduleName);

            switch (customId) {
                case "start" -> {
                    final boolean b = ModuloCore.getInstance().getModuleManager().loadModule(moduleName);
                    if (!b) {
                        event.getInteraction().createImmediateResponder()
                                .setContent("Module " + moduleName + " not found!")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond();
                        return;
                    }
                    event.getInteraction().createImmediateResponder()
                            .setContent("Module " + moduleName + " loaded!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                }
                case "stop" -> {
                    if (module == null) return;
                    ModuloCore.getInstance().getModuleManager().disableModule(module);
                    event.getInteraction().createImmediateResponder()
                            .setContent("Module " + moduleName + " disabled!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                }
                case "reload" -> {
                    if (module == null) return;
                    ModuloCore.getInstance().getModuleManager().reloadModule(module);
                    event.getInteraction().createImmediateResponder()
                            .setContent("Module " + moduleName + " reloaded!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                }
                case "delete" -> {
                    if (module == null) return;
                    ModuloCore.getInstance().getModuleManager().deleteModule(module);
                    event.getMessageComponentInteraction().getMessage().delete();
                    event.getInteraction().createImmediateResponder()
                            .setContent("Module " + moduleName + " deleted!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                }
            }
        }
    }
}
