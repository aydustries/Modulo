package fr.aytronn.moduloapi.command;

import org.javacord.api.interaction.SlashCommandOptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String description() default "Empty description";

    SlashCommandOptionType[] subCommandType() default {};

    String[] subCommand() default {};

    boolean required() default true;
}
