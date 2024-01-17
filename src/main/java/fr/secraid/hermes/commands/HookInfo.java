package fr.secraid.hermes.commands;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class HookInfo {
    private final String customId;
    private final Pattern pattern;
    private final HookTarget target;
    private final Method method;
    private final Object instance;

    protected HookInfo(String customId, HookTarget target, Method method, Object instance) {
        this.customId = customId;
        this.pattern = null;
        this.target = target;
        this.method = method;
        this.instance = instance;
    }

    protected HookInfo(Pattern pattern, HookTarget target, Method method, Object instance) {
        this.customId = null;
        this.pattern = pattern;
        this.target = target;
        this.method = method;
        this.instance = instance;
    }

    public String getCustomId() {
        return customId;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean matches(String s) {
        if (pattern == null) throw new IllegalStateException("This hook info is bound to a custom id");
        return pattern.matcher(s).matches();
    }

    public HookTarget getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public enum HookTarget {
        BUTTON,
        SELECT_MENU,
        MODAL
    }
}
