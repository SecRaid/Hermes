package fr.secraid.hermes.commands;

import fr.secraid.hermes.utils.StringUtils;
import fr.secraid.hermes.utils.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class CommandMapper extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandMapper.class);
    private final List<CommandInfo> commands = new ArrayList<>();
    private final Map<Class<?>, Object> customContexts = new HashMap<>();
    private final List<HookInfo> hooks = new ArrayList<>();
    private boolean ready = false;

    /**
     * Create a new <code>CommandMapper</code> instance. This is the base of the slash command system.
     * <b>Note: You need to manually register it as an event listener to your JDA/ShardManager instance</b>
     *
     * @param packageName The path to the package containing your commands. The commands will be imported via reflection
     */
    public CommandMapper(String packageName) {
        Set<Class<?>> classes = findAllClassesInPackage(packageName);
        for (Class<?> aClass : classes) {
            Object instance = null;
            try {
                for (Method method : aClass.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Command.class)) {
                        if (instance == null) instance = aClass.getConstructor().newInstance();
                        Command command = method.getAnnotation(Command.class);
                        commands.add(new CommandInfo(
                                aClass,
                                instance,
                                method,
                                command
                        ));
                    }
                    if (method.isAnnotationPresent(ButtonHook.class)) {
                        ButtonHook buttonHook = method.getAnnotation(ButtonHook.class);
                        if (instance == null) instance = aClass.getConstructor().newInstance();
                        if (buttonHook.enableMatching()) hooks.add(new HookInfo(
                                Pattern.compile(buttonHook.value()),
                                HookInfo.HookTarget.BUTTON,
                                method,
                                instance
                        ));
                        else hooks.add(new HookInfo(buttonHook.value(), HookInfo.HookTarget.BUTTON, method, instance));
                    }
                    if (method.isAnnotationPresent(SelectMenuHook.class)) {
                        SelectMenuHook selectMenuHook = method.getAnnotation(SelectMenuHook.class);
                        if (instance == null) instance = aClass.getConstructor().newInstance();
                        if (selectMenuHook.enableMatching()) hooks.add(new HookInfo(
                                Pattern.compile(selectMenuHook.value()),
                                HookInfo.HookTarget.SELECT_MENU,
                                method,
                                instance
                        ));
                        else hooks.add(new HookInfo(selectMenuHook.value(), HookInfo.HookTarget.SELECT_MENU,
                                method, instance));
                    }
                    if (method.isAnnotationPresent(ModalHook.class)) {
                        ModalHook modalHook = method.getAnnotation(ModalHook.class);
                        if (instance == null) instance = aClass.getConstructor().newInstance();
                        if (modalHook.enableMatching()) hooks.add(new HookInfo(
                                Pattern.compile(modalHook.value()),
                                HookInfo.HookTarget.MODAL,
                                method,
                                instance
                        ));
                        else hooks.add(new HookInfo(modalHook.value(), HookInfo.HookTarget.MODAL,
                                method, instance));
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("Cannot instantiate class {}", aClass.getName(), e);
            }
        }
    }

    @NotNull
    private List<OptionData> getOptions(@NotNull CommandInfo command) {
        List<OptionData> options = new LinkedList<>();
        for (Parameter parameter : command.getMethod().getParameters()) {
            if (!parameter.isAnnotationPresent(CommandOption.class)) continue;
            CommandOption commandOption = parameter.getAnnotation(CommandOption.class);
            Class<?> parameterType = parameter.getType();
            OptionType optionType = commandOption.type();
            if (optionType == OptionType.UNKNOWN) {
                if (parameterType == Message.Attachment.class) optionType = OptionType.ATTACHMENT;
                else if (parameterType == Boolean.class) optionType = OptionType.BOOLEAN;
                else if (GuildChannel.class.isAssignableFrom(parameterType)) optionType = OptionType.CHANNEL;
                else if (parameterType == Double.class) optionType = OptionType.NUMBER;
                else if (parameterType == Integer.class) optionType = OptionType.INTEGER;
                else if (parameterType == Long.class) optionType = OptionType.INTEGER;
                else if (parameterType == Member.class) optionType = OptionType.USER;
                else if (parameterType == IMentionable.class) optionType = OptionType.MENTIONABLE;
                else if (parameterType == Role.class) optionType = OptionType.ROLE;
                else if (parameterType == String.class) optionType = OptionType.STRING;
                else if (parameterType == User.class) optionType = OptionType.USER;
                else if (parameterType == Mentions.class) optionType = OptionType.STRING;
                else throw new UnsupportedOperationException("Invalid class for option");
            }
            OptionData option = new OptionData(
                    optionType,
                    StringUtils.isEmpty(commandOption.value()) ? TextUtils.normalizeCommandName(parameter.getName())
                            : commandOption.value(),
                    commandOption.description(),
                    commandOption.required()
            );

            if (optionType == OptionType.CHANNEL) option.setChannelTypes(commandOption.channelType().getChannelTypes());
            if (commandOption.type() == OptionType.UNKNOWN && parameter.getType() == Integer.class)
                option.setMinValue(Integer.MIN_VALUE).setMinValue(Integer.MAX_VALUE);
            if (commandOption.minValue() != Long.MIN_VALUE) option.setMinValue(commandOption.minValue());
            if (commandOption.maxValue() != Long.MAX_VALUE) option.setMinValue(commandOption.maxValue());

            options.add(option);
        }
        return options;
    }

    /**
     * Register a custom context, allowing you to call it inside your commands handles. If a context of the same class exists, it will be overwritten
     *
     * @param o The custom context you want to add
     */
    public void addCustomContext(Object o) {
        customContexts.put(o.getClass(), o);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (ready) return;
        ready = true;
        Map<String, SlashCommandData> commandData = new HashMap<>();

        for (CommandInfo command : commands) {
            String name = command.getName();
            String desc = command.getCommand().description();
            if (command.isSubcommand()) {
                CommandGroup parentCommand = command.getParentCommand();
                String parentName = parentCommand.value();
                String parentDesc = parentCommand.description();
                if (!commandData.containsKey(parentName)) commandData.put(parentName,
                        Commands.slash(parentName, parentDesc)
                                .setGuildOnly(!parentCommand.dm())
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(parentCommand.permissions())));
                commandData.get(parentName).addSubcommands(new SubcommandData(name, desc)
                        .addOptions(getOptions(command)));
            } else commandData.put(name, Commands.slash(name, desc)
                    .setGuildOnly(!command.getCommand().dm())
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getCommand().permissions()))
                    .addOptions(getOptions(command))
            );
        }

        JDA jda = event.getJDA();
        List<net.dv8tion.jda.api.interactions.commands.Command> jdaCommands = jda.updateCommands()
                .addCommands(commandData.values())
                .complete();
        for (net.dv8tion.jda.api.interactions.commands.Command jdaCommand : jdaCommands) {
            for (CommandInfo command : commands) {
                if (command.getName().equals(jdaCommand.getName())) {
                    command.setJdaCommand(jdaCommand);
                    break;
                }
            }
        }
    }

    @NotNull
    private Set<Class<?>> findAllClassesInPackage(@NotNull String packageName) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes.filterResultsBy(c -> true));
        HashSet<Class<?>> classes = new HashSet<>(reflections.getSubTypesOf(Object.class));
        logger.debug("Found {} classes using reflections", classes.size());
        return classes;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        for (CommandInfo command : commands) {
            if (!event.getName().equals(command.isSubcommand() ? command.getParentCommand().value() : command.getName()))
                continue;
            String sub = event.getSubcommandName();
            if (command.isSubcommand() && (sub == null || !command.getName().equals(sub))) continue;
            InteractionHook hook = null;
            if (command.getCommand().autoDefer()) hook = event.deferReply().complete();
            Parameter[] parameters = command.getMethod().getParameters();
            Object[] objects = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> parameterType = parameter.getType();
                Object object;
                if (parameter.isAnnotationPresent(CommandOption.class)) {
                    CommandOption commandOption = parameter.getAnnotation(CommandOption.class);
                    OptionMapping mapping = event.getOption(commandOption.value());
                    if (mapping == null) object = null;
                    else if (parameterType == Message.Attachment.class) object = mapping.getAsAttachment();
                    else if (parameterType == Boolean.class) object = mapping.getAsBoolean();
                    else if (GuildChannel.class.isAssignableFrom(parameterType)) object = mapping.getAsChannel();
                    else if (parameterType == Double.class) object = mapping.getAsDouble();
                    else if (parameterType == Integer.class) object = mapping.getAsInt();
                    else if (parameterType == Long.class) object = mapping.getAsLong();
                    else if (parameterType == Member.class) object = mapping.getAsMember();
                    else if (parameterType == IMentionable.class) object = mapping.getAsMentionable();
                    else if (parameterType == Role.class) object = mapping.getAsRole();
                    else if (parameterType == String.class) object = mapping.getAsString();
                    else if (parameterType == User.class) object = mapping.getAsUser();
                    else if (parameterType == Mentions.class) object = mapping.getMentions();
                    else throw new UnsupportedOperationException("Invalid class for option");
                } else {
                    if (customContexts.containsKey(parameterType)) object = customContexts.get(parameterType);
                    else if (parameterType == SlashCommandInteractionEvent.class) object = event;
                    else if (parameterType == InteractionHook.class) object = hook;
                    else if (parameterType == User.class) object = event.getUser();
                    else if (parameterType == Member.class) object = event.getMember();
                    else if (parameterType == Guild.class) object = event.getGuild();
                    else if (MessageChannel.class.isAssignableFrom(parameterType)) object = event.getChannel();
                    else if (parameterType == JDA.class) object = event.getJDA().getShardManager();
                    else if (parameterType == ShardManager.class) object = event.getJDA();
                    else throw new UnsupportedOperationException("Unsupported parameter type");
                }
                objects[i] = object;
            }
            try {
                command.getMethod().invoke(command.getInstance(), objects);
            } catch (IllegalAccessException e) {
                logger.error("Cannot access command {}", event.getFullCommandName(), e);
            } catch (InvocationTargetException e) {
                logger.error("Encountered unexpected error while executing command {}", event.getFullCommandName(), e);
                if (!event.isAcknowledged()) event.reply(String.format(
                        ":x: Encountered exception while executing command. Please try again later\nError:\n```%s```",
                        e.getMessage()
                )).setEphemeral(true).queue();
                else event.getChannel().sendMessage(String.format(
                        ":x: Encountered exception while executing command. Please try again later\nError:\n```%s```",
                        e.getMessage()
                )).queue(noop, noop);
            }
        }
    }

    @Nullable
    private HookInfo getMatchingHook(String id, HookInfo.HookTarget target) {
        for (HookInfo hook : hooks) {
            if (!hook.getTarget().equals(target)) continue;
            if ((hook.getPattern() != null && hook.matches(id)) ||
                    (hook.getCustomId() != null && hook.getCustomId().equals(id))) return hook;
        }
        return null;
    }

    private void processHooks(@NotNull GenericInteractionCreateEvent event, HookInfo.HookTarget target) {
        String customId;
        if (event instanceof GenericComponentInteractionCreateEvent)
            customId = ((GenericComponentInteractionCreateEvent) event).getComponentId();
        else if (event instanceof ModalInteractionEvent) customId = ((ModalInteractionEvent) event).getModalId();
        else throw new IllegalArgumentException("This event is not supported by this method");

        HookInfo hook = getMatchingHook(customId, target);
        if (hook != null) {
            Parameter[] parameters = hook.getMethod().getParameters();
            Object[] objects = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> parameterType = parameter.getType();
                Object object;

                if (customContexts.containsKey(parameterType)) object = customContexts.get(parameterType);
                else if (parameterType == event.getClass()) object = event;
                else if (parameterType == User.class) object = event.getUser();
                else if (parameterType == Member.class) object = event.getMember();
                else if (parameterType == Guild.class) object = event.getGuild();
                else if (MessageChannel.class.isAssignableFrom(parameterType)) object = event.getChannel();
                else if (parameterType == JDA.class) object = event.getJDA();
                else if (parameterType == ShardManager.class) object = event.getJDA().getShardManager();
                else if (parameter.isAnnotationPresent(CustomId.class)) object = customId;
                else throw new UnsupportedOperationException("Unsupported parameter type");
                objects[i] = object;
            }
            try {
                hook.getMethod().invoke(hook.getInstance(), objects);
            }  catch (IllegalAccessException e) {
                logger.error("Cannot access hook {}", customId, e);
            } catch (InvocationTargetException e) {
                logger.error("Encountered unexpected error while executing hook {}", customId, e);
                if (event instanceof GenericComponentInteractionCreateEvent componentEvent) {
                    if (!componentEvent.isAcknowledged()) componentEvent.reply(String.format(
                            ":x: Encountered exception while processing your response. Please try again later\nError:\n```%s```",
                            e.getMessage()
                    )).setEphemeral(true).queue();
                    else componentEvent.getChannel().sendMessage(String.format(
                            ":x: Encountered exception while processing your response. Please try again later\nError:\n```%s```",
                            e.getMessage()
                    )).queue(noop, noop);
                }

            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        processHooks(event, HookInfo.HookTarget.BUTTON);
    }

    @Override
    public void onGenericSelectMenuInteraction(@NotNull GenericSelectMenuInteractionEvent event) {
        processHooks(event, HookInfo.HookTarget.SELECT_MENU);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        processHooks(event, HookInfo.HookTarget.MODAL);
    }

    /**
     * @return true if the mapper is ready and have started the command registration process, false otherwise
     */
    public boolean isReady() {
        return ready;
    }

    private static final Consumer<Object> noop = o -> {};
}
