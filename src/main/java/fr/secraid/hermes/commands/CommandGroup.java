package fr.secraid.hermes.commands;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandGroup {
    /**
     * The command group name
     * @return The command group name
     */
    String value();
    /**
     * The command group description, defaults to "No description provided"
     * @return The command group description, defaults to "No description provided"
     */
    String description() default "No description provided";

    /**
     * Determine if the command group can be executed in DMs, defaults to false
     * @return Determine if the command group can be executed in DMs, defaults to false
     */
    boolean dm() default false;

    /**
     * The permissions needed to execute the command group. EXCLUSIVE
     * @return The permissions needed to execute the command group. EXCLUSIVE
     */
    Permission[] permissions() default {};
    /**
     * The guild where the command should be registered, defaults to Long.MIN_VALUE meaning the mapper will register the command as a global command
     * @return The guild where the command should be registered, defaults to Long.MIN_VALUE meaning the mapper will register the command as a global command
     */
    long guild() default Long.MIN_VALUE;
}
