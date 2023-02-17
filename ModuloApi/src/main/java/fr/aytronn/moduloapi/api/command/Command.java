package fr.aytronn.moduloapi.api.command;

import org.javacord.api.interaction.SlashCommandOptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The name of the command (ex: modulo.module.add)
     *
     * @return the name
     */
    String name();

    /**
     * The description of the command
     *
     * @return the description
     */
    String description() default "Empty description";

    /**
     * The type of the sub command
     *
     * @return the type
     */
    SlashCommandOptionType[] subCommandType() default {};

    /**
     * The name of the sub command
     *
     * @return the name
     */
    String[] subCommand() default {};

    /**
     * If the sub command is required
     *
     * @return if the sub command is required
     */
    boolean required() default true;
}
