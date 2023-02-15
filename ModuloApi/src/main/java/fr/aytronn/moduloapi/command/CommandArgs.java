package fr.aytronn.moduloapi.command;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;
import java.util.Optional;

public class CommandArgs {

    private final User sender;
    private final List<SlashCommandInteractionOption> args;
    private final Optional<TextChannel> channel;
    private final SlashCommandInteraction commandInteraction;

    public CommandArgs(SlashCommandInteraction commandInteraction) {
        this.commandInteraction = commandInteraction;
        this.sender = commandInteraction.getUser();
        this.args = commandInteraction.getArguments();
        this.channel = commandInteraction.getChannel();
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
     * Get the arguments of the command
     *
     * @return the arguments
     */
    public List<SlashCommandInteractionOption> getArgs() {
        return this.args;
    }

    /**
     * Get the channel where the command was sent
     *
     * @return the channel
     */
    public Optional<TextChannel> getChannel() {
        return this.channel;
    }

    /**
     * Get the command interaction
     *
     * @return the command interaction
     */
    public SlashCommandInteraction getCommandInteraction() {
        return this.commandInteraction;
    }

    /**
     * Reply to the command
     *
     * @param message the message to reply
     * @param flags   the flags to use
     */
    public void reply(String message, MessageFlag... flags) {
        this.commandInteraction.createImmediateResponder()
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
