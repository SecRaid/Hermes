package fr.secraid.hermes.utils;

import org.jetbrains.annotations.NotNull;

public class TextUtils {
    @NotNull
    public static String capitalize(@NotNull String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    @NotNull
    public static String normalizeCommandName(@NotNull String s) {
        return s.replaceAll("(?<!^)(?=[A-Z])", "-").toLowerCase();
    }

    @NotNull
    public static String normalizeCamelCase(@NotNull String s) {
        return s.replaceAll("(?<!^)(?=[A-Z])", " ");
    }
}
