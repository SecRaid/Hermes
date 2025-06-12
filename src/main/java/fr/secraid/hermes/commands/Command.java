package fr.secraid.hermes.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * The command name, defaults to "", meaning the mapper will automatically generate the command name from the method name
     * @return The command name, defaults to "", meaning the mapper will automatically generate the command name from the method name
     */
    String value() default "";

    /**
     * The command description, defaults to "No description provided"
     * @return The command description, defaults to "No description provided"
     */
    String description() default "No description provided";

    @Deprecated(forRemoval = true, since = "1.0.0")
    boolean legacy() default false;

    /**
     * Determine if the command can be executed in DMs, defaults to false
     * @return Determine if the command can be executed in DMs, defaults to false
     * @deprecated use contexts()
     */
    @Deprecated(forRemoval = true, since = "1.1.0")
    boolean dm() default false;

    /**
     * @return the contexts where the command can be executed (defaults to guild only)
     */
    InteractionContextType[] contexts() default {InteractionContextType.GUILD};

    /**
     * The permissions needed to execute the command. EXCLUSIVE
     * @return The permissions needed to execute the command. EXCLUSIVE
     */
    Permission[] permissions() default {};

    /**
     * The guild where the command should be registered, defaults to Long.MIN_VALUE meaning the mapper will register the command as a global command
     * @return The guild where the command should be registered, defaults to Long.MIN_VALUE meaning the mapper will register the command as a global command
     */
    // TODO: implement
    long guild() default Long.MIN_VALUE;

    /**
     * Should the interaction be automatically deferred before executing the method. Allows you to use the InteractionHook parameter
     * @return Should the interaction be automatically deferred before executing the method. Allows you to use the InteractionHook parameter
     */
    boolean autoDefer() default false;
}
