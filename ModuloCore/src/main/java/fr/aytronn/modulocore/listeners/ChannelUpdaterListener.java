package fr.aytronn.modulocore.listeners;

import fr.aytronn.moduloapi.api.module.IModule;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class ChannelUpdaterListener implements MessageCreateListener {

    /**
     * This method is called every time a message is created.
     *
     * @param event The event.
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (ModuloCore.getInstance().getSettingsManager().getSettings() == null) return;
        final long channelUpdater = ModuloCore.getInstance().getSettingsManager().getSettings().getChannelUpdater();
        if (event.getChannel().getId() != channelUpdater) return;

        if (event.getMessage().getAttachments().isEmpty()) return;
        final MessageAttachment messageAttachment = event.getMessageAttachments().get(0);
        ModuloCore.getInstance().getModuleManager().downloadModuleFromAttachment(messageAttachment);
        final IModule module = ModuloCore.getInstance().getModuleManager().loadModule(messageAttachment.getFileName().replace(".jar", ""));

        if (module == null) {
            event.getChannel().sendMessage("The module is not valid!");
            return;
        }
        event.getMessage().reply("The module " + module.getModuleInfo().getName() + " has been loaded!").join();
    }
}
