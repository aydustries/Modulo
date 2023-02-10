package fr.aytronn.moduloapi.command;

import org.javacord.api.entity.channel.TextChannel;
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

    public User getSender() {
        return this.sender;
    }

    public List<SlashCommandInteractionOption> getArgs() {
        return this.args;
    }

    public Optional<TextChannel> getChannel() {
        return this.channel;
    }

    public SlashCommandInteraction getCommandInteraction() {
        return this.commandInteraction;
    }
}
