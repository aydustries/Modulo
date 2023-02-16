package fr.aytronn.modulocore.listeners;

import fr.aytronn.moduloapi.api.action.ActionArgs;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ActionListener implements MessageComponentCreateListener {

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        final String[] split = event.getMessageComponentInteraction().getCustomId().split("@@");
        if (split.length < 1) return;
        final String customId = split[0];
        final Map.Entry<Method, Object> method = ModuloCore.getInstance().getActionManager().getMethods().get(customId);
        if (method == null) return;

        final List<String> args = Arrays.stream(split).skip(1).toList();
        try {
            method.getKey().invoke(method.getValue(), new ActionArgs(event, args));
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
