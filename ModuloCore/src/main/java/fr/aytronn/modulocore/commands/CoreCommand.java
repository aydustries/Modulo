package fr.aytronn.modulocore.commands;

import fr.aytronn.moduloapi.command.Command;
import fr.aytronn.moduloapi.command.CommandArgs;
import fr.aytronn.modulocore.ModuloCore;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.SlashCommandOptionType;

public class CoreCommand {

    @Command(name = "modulo.stop", description = "Stop the bot", subCommandType = {SlashCommandOptionType.LONG}, subCommand = {"time"}, required = false)
    public void stopCommand(CommandArgs args) {
        if (args.getCommandInteraction().getArguments().size() > 0) {
            final long time = args.getCommandInteraction().getArguments().get(0).getLongValue().orElse(0L);
            if (time > 0) {
                args.getCommandInteraction().createImmediateResponder()
                        .setContent("ModuloCore is stopping in " + time + "s...")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();

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

        args.getCommandInteraction().createImmediateResponder()
                .setContent("ModuloCore is stopping...")
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
        ModuloCore.getInstance().stop();
    }
}
