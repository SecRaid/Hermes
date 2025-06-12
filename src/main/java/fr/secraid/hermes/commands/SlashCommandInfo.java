package fr.secraid.hermes.commands;

import fr.secraid.hermes.utils.StringUtils;
import fr.secraid.hermes.utils.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class SlashCommandInfo extends CommandInfo<Command> {
    protected SlashCommandInfo(@NotNull Class<?> commandClass, Object instance, Method method, Command command) {
        super(commandClass, instance, method, command);
    }

    public boolean isGuildCommand() {
        return getCommand().guild() != Long.MIN_VALUE;
    }

    public String getName() {
        return StringUtils.isEmpty(getCommand().value()) ? TextUtils.normalizeCommandName(getMethod().getName())
                : getCommand().value();
    }

    public boolean isSubcommand() {
        return getCommandClass().isAnnotationPresent(CommandGroup.class);
    }

    public CommandGroup getParentCommand() {
        if (!isSubcommand()) return null;
        return getCommandClass().getAnnotation(CommandGroup.class);
    }
}
