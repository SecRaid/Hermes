package fr.secraid.hermes.commands;

import fr.secraid.hermes.utils.StringUtils;
import fr.secraid.hermes.utils.TextUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class CommandInfo {
    private final Class<?> commandClass;
    private final Object instance;
    private final Method method;
    private final Command command;
    private net.dv8tion.jda.api.interactions.commands.Command jdaCommand;

    protected CommandInfo(@NotNull Class<?> commandClass, Object instance, Method method, Command command) {
        this.commandClass = commandClass;
        this.instance = instance;
        this.command = command;
        this.method = method;
    }

    public Class<?> getCommandClass() {
        return commandClass;
    }

    public Object getInstance() {
        return instance;
    }

    public net.dv8tion.jda.api.interactions.commands.Command getJdaCommand() {
        return jdaCommand;
    }

    protected void setJdaCommand(net.dv8tion.jda.api.interactions.commands.Command jdaCommand) {
        this.jdaCommand = jdaCommand;
    }

    protected Method getMethod() {
        return method;
    }

    public Command getCommand() {
        return command;
    }

    public boolean isSubcommand() {
        return commandClass.isAnnotationPresent(CommandGroup.class);
    }

    public CommandGroup getParentCommand() {
        if (!isSubcommand()) return null;
        return commandClass.getAnnotation(CommandGroup.class);
    }

    public String getName() {
        return StringUtils.isEmpty(command.value()) ? TextUtils.normalizeCommandName(method.getName()) : command.value();
    }
}
