package fr.secraid.hermes.commands;

import net.dv8tion.jda.api.Permission;

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
    @Deprecated
    boolean legacy() default false;

    /**
     * Determine if the command can be executed in DMs, defaults to false
     * @return Determine if the command can be executed in DMs, defaults to false
     */
    boolean dm() default false;

    /**
     * The permissions needed to execute the command. EXCLUSIVE
     * @return The permissions needed to execute the command. EXCLUSIVE
     */
    Permission[] permissions() default {};

    /**
     * The guild where the command should be registered, defaults to Long.MIN_VALUE meaning the mapper will register the command as a global command
     * @return The guild where the command should be registered, defaults to Long.MIN_VALUE meaning the mapper will register the command as a global command
     */
    long guild() default Long.MIN_VALUE;

    /**
     * Should the interaction be automatically deferred before executing the method. Allows you to use the InteractionHook parameter
     * @return Should the interaction be automatically deferred before executing the method. Allows you to use the InteractionHook parameter
     */
    boolean autoDefer() default false;
}
