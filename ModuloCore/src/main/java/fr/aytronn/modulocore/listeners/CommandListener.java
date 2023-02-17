package fr.aytronn.modulocore.listeners;

import fr.aytronn.moduloapi.api.command.CommandArgs;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class CommandListener implements SlashCommandCreateListener {

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        final Map.Entry<Method, Object> method = ModuloCore.getInstance().getCommandManager().getMethods().get(event.getSlashCommandInteraction().getFullCommandName().toLowerCase().replace(" ", "."));
        if (method == null) return;
        try {
            method.getKey().invoke(method.getValue(), new CommandArgs(event.getSlashCommandInteraction()));
            event.getInteraction().respondLater();
        } catch (IllegalAccessException | InvocationTargetException e) {
            event.getInteraction().createImmediateResponder()
                    .setContent("An error is produced retry later!")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            e.printStackTrace();
        }
    }
}
