package fr.aytronn.moduloapi.api.action;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;

import java.util.List;

public class ActionArgs {

    private final User sender;
    private final List<String> args;
    private final TextChannel channel;
    private final MessageComponentCreateEvent event;

    public ActionArgs(MessageComponentCreateEvent event, List<String> args) {
        this.event = event;
        this.sender = event.getMessageComponentInteraction().getUser();
        this.args = args;
        this.channel = event.getMessageComponentInteraction().getChannel().orElse(null);
    }

    /**
     * Get the sender of the command
     *
     * @return the sender
     */
    public User getSender() {
        return this.sender;
    }

    /**
     * Get the arguments of the action
     *
     * @return the arguments
     */
    public List<String> getArgs() {
        return this.args;
    }

    /**
     * Get the channel of the action
     *
     * @return the channel
     */
    public TextChannel getChannel() {
        return this.channel;
    }

    /**
     * Get the event of the action
     *
     * @return the event
     */
    public MessageComponentCreateEvent getEvent() {
        return this.event;
    }

    /**
     * Reply to the command
     *
     * @param message the message to reply
     * @param flags   the flags to use
     */
    public void reply(String message, MessageFlag... flags) {
        getEvent().getInteraction().createImmediateResponder()
                .setContent(message)
                .setFlags(flags)
                .respond();
    }

    /**
     * Reply to the command
     *
     * @param message the message to reply
     */
    public void reply(String message) {
        reply(message, MessageFlag.EPHEMERAL);
    }
}
