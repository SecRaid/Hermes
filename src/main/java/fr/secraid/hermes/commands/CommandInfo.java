package fr.secraid.hermes.commands;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class CommandInfo<T> {
    private final Class<?> commandClass;
    private final Object instance;
    private final Method method;
    private final T command;
    private net.dv8tion.jda.api.interactions.commands.Command jdaCommand;

    protected CommandInfo(@NotNull Class<?> commandClass, Object instance, Method method, T command) {
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

    public T getCommand() {
        return command;
    }
}
