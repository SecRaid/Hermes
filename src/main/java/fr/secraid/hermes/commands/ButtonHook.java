package fr.secraid.hermes.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ButtonHook {
    /**
     * @return The custom id of the button
     */
    String value();

    /**
     * @return If the hook value should be treated as a regex pattern for matching the custom id
     */
    boolean enableMatching() default false;

}
