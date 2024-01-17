package fr.secraid.hermes.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandOption {
    /**
     * The command option name
     * @return The command option name
     */
    String value();

    /**
     * The command option description, defaults to "No description provided"
     * @return The command option description, defaults to "No description provided"
     */
    String description() default "No description provided";

    /**
     * The command option type, defaults to UNKNOWN, meaning the mapper will try to automatically determine the command type
     * @return The command option type, defaults to UNKNOWN, meaning the mapper will try to automatically determine the command type
     */
    OptionType type() default OptionType.UNKNOWN;

    GlobalChannelType channelType() default GlobalChannelType.ALL;
    boolean required() default true;
    double minValue() default Long.MIN_VALUE;
    double maxValue() default Long.MAX_VALUE;
}
