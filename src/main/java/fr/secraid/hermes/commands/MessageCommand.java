package fr.secraid.hermes.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageCommand {
    /**
     * @return The message command name, defaults to "", meaning the mapper will automatically generate the command name from the method name
     */
    String value() default "";
}
